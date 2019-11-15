# jbpm-android


jBPM is a flexible Business Process Management (BPM) Suite. It makes the bridge between business analysts and developers.
The jBPM can be accessed by a web based workbench. But the problem is that jBPM cannot be accessed by mobile users. Therefore
the idea of the project is to create a mobile UI of the jBPM-console where mobile users can interact some of the features of 
the jBPM-console. 
The jBPM core engine itself is a lightweight workflow engine. Therefore it can be run on android as well. Therefore apart 
from the mobile UI, a prototype of jBPM on android will be also created where user can interact withoutconnect to the 
jBPM-console.
For more details and everything, visit the blog for pogress of the project.
      https://supunasp.blogspot.com/

This app uses the remote REST API to connect to the execution server of the jBPM Console and execute the Evaluation process there.
 
 This prototype assumes: - you have the jbpm console running at
 http://localhost:8080/jbpm-console (automatically when using jbpm-installer)
  - you have users krisv/krisv, john/john and mary/mary (automatically whenusing jbpm-installer) - you have deployed the Evaluation project (part of the jbpm-playground)
  - you have correctly input the username and password.

This is an android Studio project.
Therefore you can use this app and run the LoginScreen activity as default one.
 
