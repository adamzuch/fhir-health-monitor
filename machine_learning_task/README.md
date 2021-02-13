## Running order of Acquiring Data Scripts in RUN_ALL.py file
###### _Located in **ACQUIRING_DATA/src/**_

###### _(Timings are for when 1000 patient IDs are to be acquired)_

###### _Timing of the entire operation of the RUN_ALL.py file is ~66 seconds_
 
### **1.** GetPatientIDList_1.py   _~14 seconds_
Acquires the number of specified IDs of the patients from the server. The default
number of ids is 1000.  

### **2.** GetPatientData_2.py _~42 seconds_ 
From the patient ids specified from the previous script, this script uses the ids to 
search for patients. It also doesn't retrieve patients that a unclear in their format.

### **3.** ProcessPatientData_3.py  _~ 0.2 seconds_ 
Runs through the patients acquired in the previous steps and drops any patients 
that are missing cholesterol data. It also drops any patients that dont have the 
health data that more than 60% of the patients have.

### **4.** ProcessingHealthData_4.py _~0.07 seconds_ 
Reformats the patient's health data into a more readable format and a format in
which more data can be added in a a scalable manner.

### **5.** AddingMoreNonHealthFeatures_5.py _~8 seconds_
The script runs through the patients that have been acquired previously and have 
the health data and adds non health data such as age, race and gender. 

### **6.** PresentingData_6.py _~0.3 seconds_
This final script simply reformats the data into a human readable HTML file and 
a csv file that can be loaded by the machine learning script.

## Further Information
* Please read the LOG.txt file located in _**ACQUIRING_DATA/**_ to see the output of previous
runnings of the RUN_ALL.py script. 
* Please use the static HTML file (_CreatingMachineLearningModel.html_) to view the process 
in which the ML model was made. It is not reccomended to re-run the jupyter file from 
which it originated since **the time the script takes to run is undetermined**.