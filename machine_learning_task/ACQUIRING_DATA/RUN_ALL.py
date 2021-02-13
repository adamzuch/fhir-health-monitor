import time
import sys
from ACQUIRING_DATA.src import GetPatientIDList_1, GetPatientData_2, ProcessPatientData_3, ProcessingHealthData_4, \
    AddingMoreNonHealthFeatures_5, PresentingData_6
from datetime import datetime

"""
Logger class needed to print to both the terminal and save console to the log file.
"""

class Logger(object):
    def __init__(self):
        self.terminal = sys.stdout
        self.log = open("LOG.txt", "a")

    def write(self, message):
        self.terminal.write(message)
        self.log.write(message)

    def flush(self):
        #this flush method is needed for python 3 compatibility.
        #this handles the flush command by doing nothing.
        #you might want to specify some extra behavior here.
        pass

sys.stdout = Logger()

def main():



    #Time how long this takes
    start_time = time.time()
    print("\n======================================================================================")

    #Record the Date and time
    now = datetime.now()
    dt_string = now.strftime("%d/%m/%Y %H:%M:%S")
    print(dt_string)

    print("Running Acquiring Data for machine model - ")

    GetPatientIDList_1.main()
    GetPatientData_2.main()
    ProcessPatientData_3.main()
    ProcessingHealthData_4.main()
    AddingMoreNonHealthFeatures_5.main()
    PresentingData_6.main()


    print("Entire Operation has taken--- %s seconds ---" % (time.time() - start_time))

if __name__ == "__main__":
    main()