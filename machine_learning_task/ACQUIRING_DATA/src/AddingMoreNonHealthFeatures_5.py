#!/usr/bin/env python
# coding: utf-8

# Load in all the patients health data to add more non health features to it.

# In[80]:


import json
import requests
import pprint
import datetime
import requests
import os
from queue import Queue
from threading import Thread
import time
import json

from dateutil.relativedelta import relativedelta


def getPatientAge(data,patient):
    date_str = data['birthDate'] 
    format_str = '%Y-%m-%d' # The format
    start_date = datetime.datetime.strptime(date_str, format_str)
    """
    See if they are deceased, if so the end date is when they died
    """
    try:
        end_str = data['deceasedDateTime']
        end_str = end_str.split("T")[0]
        format_str = '%Y-%m-%d' # The format
        end_date = datetime.datetime.strptime(end_str, format_str)
    except KeyError:
        #If they are still alive then take the end date to be today
        end_date = datetime.date.today()

    """
    Their age would be the difference between the start date and end date
    """
    patient["age"] = relativedelta(end_date, start_date).years


def getPatientGender(data,patient):
    if data['gender'] =="male":
        patient['gender']=1
    else:
        patient['gender']=0


"""
Now we try and get their race. According to the FHIR there are 5 OMB race categories: American Indian or Alaska Native - Asian - Black or African American - 
Native Hawaiian or Other Pacific Islander - 
White we will assign each to be a different features such that we can use one hot encoding method.
American Indian = AI, Asian = AS, Black or African American = AA , Native Hawaiian or Other Pacific Islander = NH, White = W
and Other Race = OR
"""
def getPatientRace(data,patient):
    code_to_raceMap = {
    "1002-5":"AI",
    "2028-9":"AS",
    "2054-5":"AA",
    "2076-8":"NH",
    "2106-3":"W",
    }
    race_to_codeMap = {
        "AI":"1002-5",
        "AS":"2028-9",
        "AA":"2054-5",
        "NH":"2076-8",
        "W":"2106-3",
        "OR":"2131-1"
    }
    if data["extension"][0]['url']=='http://hl7.org/fhir/us/core/StructureDefinition/us-core-race':
        raceCode = data["extension"][0]['extension'][0]['valueCoding']['code']
        """
        Perform the one hot encoding
        """
        if raceCode==race_to_codeMap["AI"]:
            AI = 1
            AS = 0
            AA = 0
            NH = 0
            W = 0
            OR=0
        elif raceCode==race_to_codeMap["AS"]:
            AI = 0
            AS = 1
            AA = 0
            NH = 0
            W = 0
            OR=0

        elif raceCode==race_to_codeMap["AA"]:
            AI = 0
            AS = 0
            AA = 1
            NH = 0
            W = 0
            OR=0

        elif raceCode==race_to_codeMap["NH"]:
            AI= 0
            AS= 0
            AA= 0
            NH = 1
            W = 0
            OR=0

        elif raceCode==race_to_codeMap["W"]:
            AI= 0
            AS= 0
            AA= 0
            NH = 0
            W = 1
            OR=0
        elif raceCode == race_to_codeMap["OR"]:
            AI= 0
            AS= 0
            AA= 0
            NH = 0
            W = 0
            OR=1
        else:
            print(raceCode)
            raise ValueError
        patient["AI"] =AI
        patient["AS"] =AS
        patient["AA"] =AA
        patient["NH"] =NH
        patient["W"] =W
        patient["OR"] =OR


def addAdditionalData(patient):
    root_url = 'https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/'
    patients_url = root_url +"Patient/"
    patient_id_url = patients_url + patient["ID"]
    data = requests.get(url=patient_id_url).json()
    #If it doesnt fit this format then mark the patient for removal
    try:
        getPatientRace(data,patient)
        getPatientAge(data,patient)
        getPatientGender(data,patient)
        patient["removal"] = False
    except KeyError:
        patient["removal"] = True

class DownloadWorker(Thread):
    def __init__(self, queue):
        Thread.__init__(self)
        self.queue = queue

    def run(self, ):
        while True:
            # Get the work from the queue and expand the tuple
            patient = self.queue.get()
            try:
                addAdditionalData(patient)
            finally:
                self.queue.task_done()

def main():

    print("\nAdding more Non-Health features to the patients running -")
    with open("ALL_PATIENTS_HEALTH_DATA_ML.json") as json_file:
        ALL_PATIENTS_HEALTH_DATA = json.load(json_file)

    #Time how long this takes
    start_time = time.time()

    # Create a queue to communicate with the worker threads
    queue = Queue()

    # Create 8 worker threads
    THREADS=8
    for x in range(THREADS):
        worker = DownloadWorker(queue)
        # Setting daemon to True will let the main thread exit even though the workers are blocking
        worker.daemon = True
        worker.start()

    for patient in ALL_PATIENTS_HEALTH_DATA:
        queue.put(patient)

    # Causes the main thread to wait for the queue to finish processing all the tasks
    queue.join()

    """
    Add all the patients that haven't been marked for removal into a new array
    """
    ALL_PATIENTS_HEALTH_AND_NON_HEALTH_DATA =[]
    for patient in ALL_PATIENTS_HEALTH_DATA:
        if patient["removal"]==False:
            ALL_PATIENTS_HEALTH_AND_NON_HEALTH_DATA.append(patient)

    #Save this data
    with open("ALL_PATIENTS_HEALTH_AND_NON_HEALTH_DATA.json", "w") as write_file:
        json.dump(ALL_PATIENTS_HEALTH_AND_NON_HEALTH_DATA, write_file)

    print("Final Number of Patients: "+ str(len(ALL_PATIENTS_HEALTH_AND_NON_HEALTH_DATA)) )
    print("--- %s seconds ---" % (time.time() - start_time))

if __name__ == "__main__":
    main()




