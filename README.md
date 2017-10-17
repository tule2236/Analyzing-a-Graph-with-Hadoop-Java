# Analyzing a Graph with Hadoop Java

Design a MapReduce algorithm that accomplishes the following task: given an unordered collection of two kinds of records, the algorithm will join (combine) record pairs from the collection. In practice, if you need join a lot of data on Hadoop, you would typically higher level tools like Hive and Pig, instead of writing code to perform the joins yourself. Learning how to write the code here will help you gaindeeper understanding of the innerworking of the join operations, and will help you decide what kinds of joins to use under different situations.

**Input of your algorithm**:
>Student, Alice, 1234  
>Student, Bob, 1234  
>Department, 1123, CSE  
>Department, 1234, CS  
>Student, Joe, 1123  

The input is an unordered collection of records of two types: *Student* and *Department* .
The *Student* record is of the form *<Student, Name, Department_ID>*
The *Department* record is of the form *<Department, Department_ID, Department_Name>*
**Output of your algorithm**:
>1123, Joe, CSE  
1234, Bob, CS  
>1234, Alice, CS  
# Prerequisites
#### Installing Virtual Machine (VM)

  - Virtual Machine platform ([VirtualBox](www.virtualbox.com), [Cloudera](https://www.cloudera.com/downloads/quickstart_vms/5-8.html))
  - Configure the VM based on [this instruction](http://poloclub.gatech.edu/cse6242/2017spring/hw3/VMSetup.pdf)
#### Loading Data into HDFS
Now, let’s load our dataset into the HDFS (Hadoop Distributed File System), an abstract file system that stores files on clusters. Your Hadoop or Spark code will directly access files on HDFS. Paths on the HDFS look similar to those on the UNIX system, but you can’t explore them directly using standard UNIX commands. Instead, you need to use hadoop fs commands. For example
```sh
hadoop fs -ls /
```
Download the following two graph files: [graph1.tsv](http://poloclub.gatech.edu/cse6242/2017spring/hw3/graph1.tsv) (~5MB) and [graph2.tsv](http://poloclub.gatech.edu/cse6242/2017spring/hw3/graph2.tsv) (~900MB). Use the 1 2 following commands to setup a directory on the HDFS to store the two graph datasets . Please do not change the directory structure below (/user/cse6242/) since we will grade your homework using the scripts which assume the following directory structure.
```sh
sudo su hdfs
hadoop fs -mkdir /user/cse6242/
hadoop fs -chown cloudera /user/cse6242/
exit
su cloudera
hadoop fs -put path/to/graph1.tsv /user/cse6242/graph1.tsv
hadoop fs -put path/to/graph2.tsv /user/cse6242/graph1.tsv
```
Now both files - graph1.tsv and graph2.tsv are on HDFS at /user/cse6242/graph1.tsv and
/user/cse6242/graph2.tsv . To check this, try:
```sh
hadoop fs -ls /user/cse6242
```
#### Setting up Development Environments
We found that compiling and running Hadoop/Scala code can be quite complicated. So, we have prepared some skeleton code, compilation scripts, and execution scripts for you that you can use, in the HW3 skeleton folder. You should use this structure to submit your homework. In the GitHub directories, you will find **pom.xml** , **run1.sh**, **run2.sh** and the **src** directory.
* The src directory contains a main Java/Scala file that you will primarily work on. We have provided some code to help you get started. Feel free to edit it and add your files in the directory, but the main class should be Q1 and Q2 accordingly. Your code will be evaluated using the provided run1.sh and run2.sh file (details below).
* pom.xml contains the necessary dependencies and compile configurations for each
question. To compile, you can simply call Maven in the corresponding directory by this command:
```sh
mvn package
```
It will generate a single JAR file in the target directory (i.e., target/q2-1.0.jar). Again, we have provided you some necessary configurations to simplify your work for this homework, but you can edit them as long as our run script works and the code can be compiled using mvn package command.
*run1.sh, run2.sh are the script files that run your code over graph1.tsv (run1.sh) or graph2.tsv (run2.sh) and download the output to a local directory. The output files are named based on its question number and graph number (e.g. q1output1.tsv). You can use these run scripts to test your code. Note that these scripts will be used in grading.

Here’s what the above scripts do:
1. Run your JAR on Hadoop/Scala specifying the input file on HDFS (the first
argument) and output directory on HDFS (the second argument)
2. Merge outputs from output directory and download to local file system.
3. Remove the output directory on HDFS.
# Approach
#### Mapper

The mapper read in our data, extract the common key of both data types which is *Department_ID*. The one-to-one join that equal keys with same values are sent to the same reducer. Then, it remove the join key *Department_ID* from the list and re-join the data back into a single String and set the join key back in, join order and the remaining data. Now, our data structure would look like *(Department_ID, [Student, Student_name], [Deparment, Department_name])*.

Our first sort with TaggedKey class *Department_ID* is sorted, we have 
>(1234, [Student, Alice],  [Student, Bob] , [Department, CS]),
>(1123, [Student, Joe], [Department, CSE]). 

Then, keys with the same *Department_ID* value will have a secondary sort on the value of the *Student* or *Department* field.

Output: 
>(1234, Alice, CS),  
(1234, Bob, CS),  
>(1123, Joe, CSE)  

#### Reducer:

This is the step that joining data happening. Since the key with the tag of “1” reached the reducer first, we know that the student *name* is the first value and the *department_name* is the second. Therefore, we don’t need to keep track of any key. We simply loop over the values and concatenate them together

**Input:** 
>(1234, Alice, CS),  
(1234, Bob, CS),  
>(1123, Joe, CSE)  

**Output:**
>(1123, Joe, CSE),  
(1234, Bob, CS),  
>(1234, Alice, CS)
