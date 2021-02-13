import pprint
import shutil
from pathlib import Path

import requests
import os
from queue import Queue
from threading import Thread
import time
import json


def getPatientData(patientID,TOTAL_PATIENTS_ARRAY):
    patient_data = {}
    try:
        data1 = requests.get(url="https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Observation?patient="+str(patientID)).json()
        patient_data["ID"] = patientID
        for entry in data1['entry']:
            ##Can only get information fromm labratory and vital sign catergories
            category = entry['resource']['category'][0]['coding'][0]['code']
            if category=='vital-signs' or category=='laboratory':
                ##Make an exception for code 55284-4 which is blood pressure which has 2 components Systolic and diastolic
                resource=entry['resource']["code"]['coding'][0]
                code=resource['code']
                if code=="55284-4":
                    #Need to define a new code for each component
                    components = entry['resource']['component']
                    for component in components:
                        #pprint.pprint(component)
                        code=component["code"]['coding'][0]['code']
                        name =component["code"]['coding'][0]['display']
                        value =component['valueQuantity']['value']
                        patient_data[code]={"name":name,"value":value}

                ##Make an exception for code 85319-2 which is breast cancer
                elif code=="85319-2":
                    patient_data[code] ={"name":resource['display'],"value":entry['resource']['valueCodeableConcept']['text']}
                else:
                    try:
                        patient_data[code]={"name":resource['display'],"value":entry['resource']['valueQuantity']["value"]}
                    ##Dont include the data if there is any other conditions that dont follow the format
                    except KeyError:
                        pass
    #Nothing can be done if the data is not in the specified format
    except KeyError:
        return None
    TOTAL_PATIENTS_ARRAY.append(patient_data)
    return

def process(patientID,TOTAL_PATIENTS_ARRAY):
    getPatientData(patientID,TOTAL_PATIENTS_ARRAY)

class DownloadWorker(Thread):

    def __init__(self, queue,TOTAL_PATIENTS_ARRAY):
        Thread.__init__(self)
        self.queue = queue
        self.TOTAL_PATIENTS_ARRAY =TOTAL_PATIENTS_ARRAY

    def run(self, ):
        while True:
            # Get the work from the queue and expand the tuple
            patientID = self.queue.get()
            try:
                process(patientID,self.TOTAL_PATIENTS_ARRAY)
            finally:

                self.queue.task_done()


def main():

    print("\nGetting Patient Data (indicated by patient ID List) from server running - ")

    with open('output_files/arrayOfIDs.json') as json_file:
        patientIDList = json.load(json_file)


    #Time how long this takes
    start_time = time.time()

    # Create a queue to communicate with the worker threads
    queue = Queue()

    # Create an array thats referance gets passed onto each thread to store each patient in
    TOTAL_PATIENTS_ARRAY=[]

    # Create 8 worker threads
    THREADS=8
    for x in range(THREADS):
        worker = DownloadWorker(queue,TOTAL_PATIENTS_ARRAY)
        # Setting daemon to True will let the main thread exit even though the workers are blocking
        worker.daemon = True
        worker.start()

    for patientID in patientIDList:
        queue.put(patientID)

    # Causes the main thread to wait for the queue to finish processing all the tasks
    queue.join()

    #Delete the output file if it exists
    if os.path.exists("output_files/ALL_PATIENTS_DATA.json"):
        os.remove("output_files/ALL_PATIENTS_DATA.json")

    #Save all the patients into a file
    with open("output_files/ALL_PATIENTS_DATA.json", "w") as write_file:
        json.dump(TOTAL_PATIENTS_ARRAY, write_file)


    print("Taken--- %s seconds ---" % (time.time() - start_time))


#Run the thing
if __name__ == "__main__":
    main()




