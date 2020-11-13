import logging
from pprint import pprint
import boto3
from botocore.exceptions import ClientError
import requests
from PIL import Image, ImageDraw, ImageFont
import io
import string

class RekognitionText:
    """Encapsulates an Amazon Rekognition text element."""
    def __init__(self, text_data):
        """
        Initializes the text object.

        :param text_data: Text data, in the format returned by Amazon Rekognition
                          functions.
        """
        self.text = text_data.get('DetectedText')
        self.kind = text_data.get('Type')
        self.id = text_data.get('Id')
        self.parent_id = text_data.get('ParentId')
        self.confidence = text_data.get('Confidence')
        self.geometry = text_data.get('Geometry')

    def to_dict(self):
        """
        Renders some of the text data to a dict.

        :return: A dict that contains the text data.
        """
        rendering = {}
        if self.text is not None:
            rendering['text'] = self.text
        if self.kind is not None:
            rendering['kind'] = self.kind
        if self.geometry is not None:
            rendering['polygon'] = self.geometry.get('Polygon')
        return rendering

def show_bounding_boxes(image_bytes, box_sets, colors):
    """
    Draws bounding boxes on an image and shows it with the default image viewer.

    :param image_bytes: The image to draw, as bytes.
    :param box_sets: A list of lists of bounding boxes to draw on the image.
    :param colors: A list of colors to use to draw the bounding boxes.
    """
    image = Image.open(io.BytesIO(image_bytes))
    draw = ImageDraw.Draw(image)
    for boxes, color in zip(box_sets, colors):
        for box in boxes:
            left = image.width * box['Left']
            top = image.height * box['Top']
            right = (image.width * box['Width']) + left
            bottom = (image.height * box['Height']) + top
            draw.rectangle([left, top, right, bottom], outline=color, width=3)
    image.show()


def show_polygons(image_bytes, polygons, color):
    """
    Draws polygons on an image and shows it with the default image viewer.

    :param image_bytes: The image to draw, as bytes.
    :param polygons: The list of polygons to draw on the image.
    :param color: The color to use to draw the polygons.
    """
    image = Image.open(io.BytesIO(image_bytes))
    draw = ImageDraw.Draw(image)
    for polygon in polygons:
        draw.polygon([
            (image.width * point['X'], image.height * point['Y']) for point in polygon],
            outline=color)
    image.show()

class RekognitionImage:
    """
    Encapsulates an Amazon Rekognition image. This class is a thin wrapper
    around parts of the Boto3 Amazon Rekognition API.
    """
    def __init__(self, image, image_name, rekognition_client):
        """
        Initializes the image object.

        :param image: Data that defines the image, either the image bytes or
                      an Amazon S3 bucket and object key.
        :param image_name: The name of the image.
        :param rekognition_client: A Boto3 Rekognition client.
        """
        self.image = image
        self.image_name = image_name
        self.rekognition_client = rekognition_client

    @classmethod
    def from_file(cls, image_file_name, rekognition_client, image_name=None):
        """
        Creates a RekognitionImage object from a local file.

        :param image_file_name: The file name of the image. The file is opened and its
                                bytes are read.
        :param rekognition_client: A Boto3 Rekognition client.
        :param image_name: The name of the image. If this is not specified, the
                           file name is used as the image name.
        :return: The RekognitionImage object, initialized with image bytes from the
                 file.
        """
        with open(image_file_name, 'rb') as img_file:
            image = {'Bytes': img_file.read()}
        name = image_file_name if image_name is None else image_name
        return cls(image, name, rekognition_client)

    @classmethod
    def from_bucket(cls, s3_object, rekognition_client):
        """
        Creates a RekognitionImage object from an Amazon S3 object.

        :param s3_object: An Amazon S3 object that identifies the image. The image
                          is not retrieved until needed for a later call.
        :param rekognition_client: A Boto3 Rekognition client.
        :return: The RekognitionImage object, initialized with Amazon S3 object data.
        """
        image = {'S3Object': {'Bucket': s3_object.bucket_name, 'Name': s3_object.key}}
        return cls(image, s3_object.key, rekognition_client)

    def detect_text(self):
        """
        Detects text in the image.

        :return The list of text elements found in the image.
        """
        try:
            response = self.rekognition_client.detect_text(Image=self.image)
            texts = [RekognitionText(text) for text in response['TextDetections']]
        except ClientError:
            raise
        else:
            return texts


def main():
    #logger
    logging.basicConfig(filename='file.log',level=logging.INFO,format='%(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S %p')
    logging.getLogger().setLevel(logging.INFO)
    #Llenar Credenciales
    AWS_S3_CREDS = {
        "aws_access_key_id":"", 
        "aws_secret_access_key":"",
        "aws_session_token":""
    }

    #Handler para loggear a consola y archivo a la vez
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    logging.getLogger('').addHandler(console)
    logging.info("Procesando imagen de control...")
    
    #valores a guardar
    control_words = []
    confidence_control = []
    test_words = []
    confidence_test = []

    #Reconocemos la imagen control
    rekognition_client = boto3.client('rekognition', region_name='us-east-1', **AWS_S3_CREDS)
    control_image = 'control.png'
    control_image_object = RekognitionImage.from_file(control_image, rekognition_client)
    texts = control_image_object.detect_text()
    
    #Generamos una lista ordenada de palabras
    for text in texts:
        confidence_control += [float(text.confidence)]
        for value in text.text.split(" "):
            if len(value) > 0:
                for char in string.punctuation:
                    value = value.replace(char, '')
                control_words += [value.lower()]

    #obtenemos el confidence         
    confidence_control =  sum(confidence_control)/len(confidence_control)

    #Reconocemos la imagen nueva
    rekognition_client = boto3.client('rekognition', region_name='us-east-1', **AWS_S3_CREDS)
    control_image = input("Ingrese nombre de imagen nueva (ruta/nombre.formato):")

    logging.info("Procesando imagen de prueba...")
    control_image_object = RekognitionImage.from_file(control_image, rekognition_client)
    texts = control_image_object.detect_text()
    
    #Generamos una lista ordenada de palabras
    for text in texts:
        confidence_test += [float(text.confidence)]
        for value in text.text.split(" "):
            if len(value) > 0:
                for char in string.punctuation:
                    value = value.replace(char, '')
                test_words += [value.lower()]

    #obtenemos el confidence         
    confidence_test =  sum(confidence_test)/len(confidence_test)

    if (confidence_test < 97 or confidence_control < 97):
        logging.info("Niveles de confidencia menores a 97%. Abortando.")
        logging.info("Nivel Control:" + str(confidence_control) )
        logging.info("Nivel Prueba:" + str(confidence_test) )
    else:
        return_value = True
        for word in control_words:
            if (word not in test_words):
                return_value = False
                break

        if (return_value):
            logging.info("Todo el texto esta contenido, retornando true.")
            return True
        else:
            logging.info("Texto faltante. Retornando false.")
            return False

main()
