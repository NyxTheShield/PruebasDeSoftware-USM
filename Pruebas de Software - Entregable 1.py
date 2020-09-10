import datetime  

def LongestInput(x, y):
    try:
        currentTime = str(datetime.datetime.now()).split(".")[0]+"|"
        firstLen = len(x)
        secondLen = len(y)
        returnValue = ""
        if (firstLen > secondLen):
            returnValue = "First"
        elif (firstLen < secondLen):
            returnValue = "Second"
        else:
            returnValue = "None"
        print(currentTime+returnValue)
        return currentTime+returnValue
    except MemoryError:
        print(currentTime+"System Ran Out of Memory. Aborting")
        return(currentTime+"Error: MemoryError")
    except UnicodeEncodeError:
        print(currentTime+"Unsupported Encoding. Aborting")
        return(currentTime+"Error: UnicodeEncodeError")
    except TypeError:
        print(currentTime+"Unsupported Encoding. Aborting")
        return(currentTime+"Error: TypeError")
