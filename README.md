# SmartConversationalClient
An end to end smart conversational client, that enables low latency voice interaction, using Microsoft’s intent recognition technologies and a local smart cache.

## How to Run the Sample and Integrate the Code into a Project

### Prerequisites 
* Configure a LUIS model from the [LUIS portal](https://luis.ai).
* Clone or download the Smart Conversational Client repository [here](tbi).
* Install Android Studio from [here](https://developer.android.com/studio/index.html).
* Install at leats Android 4.2 on your device.

### Configuration

Once you complete the prerequisites open the /src/SmartConversationalClient directory in Android Studio.  

The first thing you will need to do before running the code is create a Keys resource file so that the code can connect to your LUIS application and knowledge graph.

Create a new file in '\src\samples\SpeechRecoExample\res\values' named keys.xml and then add following properties from your LUIS app:

    <string name="luisAppID">{Put LUIS AppId here}</string>
    <string name="luisSubscriptionID"> {Put LUIS SubscriptionId here}</string>
    
Once you configure these values with your own LUIS application run the demo app to ensure that everything works. To use Speech to Text ensure that you have enabled the Microphone in your devices application settings.

To integrate the SmartConverstaional API into your own JAVA application use the SmartConversationalController. The smart conversational controller requires an application context and is interacted with through the query command. 

### Sample code
```java

//Import smart conversational client controller
import com.microsoft.pct.smartconversationalclient.controller;

/*
The following code should be part of a Java function
*/

//Initialize controller
SmartConversationalController controller = new SmartConversationalController(Context.getApplicationContext());

//Query text to intent from LUIS
LUISQueryResult luisResult = (LUISQueryResult) controller.query("Go to the kitchen");

//Once a rule is learned from LUIS query text to intent from generalized rule
CacheQueryResult myResult = (CacheQueryResult) controller.query("Go to the den");

//When a knowledge graph is integrated perform Knowledge query and get spoken text
IKnowledgeQueryResult result = (IKnowledgeQueryResult) controller.query("What is the weather in tel aviv?");
String spokenText = result.getSpokenText();
```
