
import pandas as pd
import json
import numpy as np
import time

def presentHumanViewData(HUMAN_VIEW):
    with open("PatientData_HumanView.html", "w") as file:
        df = pd.DataFrame(data=HUMAN_VIEW)
        df = df.fillna(' ').T
        file.write(df.T.to_html())

def presentMLData(ML_VIEW):
    df = pd.DataFrame(data=ML_VIEW)
    df = df.fillna(' ')


    ##Save both the dataframes
    df.to_csv("PatientData_TRAIN_ML.csv", sep='\t', encoding='utf-8', index=False)

def main():
    print("\nPresenting Data into a nice format is running -")
    #Time how long this takes
    start_time = time.time()

    with open('ALL_PATIENTS_HEALTH_AND_NON_HEALTH_DATA.json') as json_file:
        VIEW =json.load(json_file)
    presentHumanViewData(VIEW)
    presentMLData(VIEW)

    print("--- %s seconds ---" % (time.time() - start_time))


if __name__ == "__main__":
    main()
