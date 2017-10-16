# Analyzing-a-Graph-with-Hadoop-Java
PROBLEM DESCRIPTION

Design a MapReduce algorithm that accomplishes the following task: given an unordered collection of
two kinds of records, the algorithm will join (combine) record pairs from the collection. In practice, if
you need join a lot of data on Hadoop, you would typically higher level tools like Hive and Pig, instead
of writing code to perform the joins yourself. Learning how to write the code here will help you gain
deeper understanding of the innerworking of the join operations, and will help you decide what kinds
of joins to use under different situations.
Input of your algorithm:

Student, Alice, 1234
Student, Bob, 1234
Department, 1123, CSE
Department, 1234, CS
Student, Joe, 1123

The input is an unordered collection of records of two types: Student and Department .
The Student record is of the form <Student, Name, Department_ID>
The Department record is of the form <Department, Department_ID, Department_Name>
Output of your algorithm:

1123, Joe, CSE
1234, Bob, CS
1234, Alice, CS

Your algorithm will join a department record and a student record if they have the same
Department_ID. Each output record is of the form <Department_ID, Name,
Department_Name> . For example, Joe’s student record Student, Joe, 1123 should be joined
with Department, 1123, CSE , producing the output record 1123, Joe, CSE .

APPROACH

Mapper
The mapper read in our data, extract the common key of both data types which is “Department_ID”. The one-to-one join that equal keys with same values are sent to the same reducer. Then, it remove the join key “Department_ID” from the list and re-join the data back into a single String and set the join key back in, join order and the remaining data. Now, our data structure would look like (Department_ID, [Student, Student_name], [Deparment, Department_name]).
Input: as example
Our first sort with TaggedKey class “Department_ID” is sorted, we have (1234, [Student, Alice], [Student, Bob] ,[Department, CS]), (1123, [Student, Joe], [Department, CSE]). Then, keys with the same “Deparment_ID” value will have a secondary sort on the value of the “Student” or “Department” field.
Output: (1234, Alice, CS) ,(1234, Bob, CS), (1123, Joe, CSE)
Reducer:
This is the step that joining data happening. Since the key with the tag of “1” reached the reducer first, we know that the student “name” is the first value and the “department_name” is the second. Therefore, we don’t need to keep track of any key. We simply loop over the values and concatenate them together
Input: (1234, Alice, CS) ,(1234, Bob, CS), (1123, Joe, CSE)
Output: (1123, Joe, CSE),
(1234, Bob, CS)
(1234, Alice, CS)
