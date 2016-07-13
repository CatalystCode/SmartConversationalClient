## How to Use

### Configuration
Once you have configured your luis model you will need to clone or download the respository from (put repo link here).

The Smart Client Demo and API currently requires at least Android 4.2 to run since this is the version of Android that supports offline Speech to Text.

Once you download the code open the /src/SmartConversationalClient directory in Android Studio. Android Studio can be downloaded and installed [here](https://developer.android.com/studio/index.html). 

The first thing you will need to do before running the code is create a Keys resource file so that the code can connect to your luis application and knowledge graph.

Create a new file in '\src\samples\SpeechRecoExample\res\values' named keys.xml and then add following properties from your luis app:

    <string name="luisAppID">{Put LUIS AppId here}</string>
    <string name="luisSubscriptionID"> {Put LUIS SubscriptionId here}</string>
    
Once you configure these valuses with your own LUIS application run the demo app to ensure that everything works. To use Speech to Text ensure that you have enabled the Microphone in your devices application settings.

To integrate the SmartConverstaional API into your own application will use the SmartConversationalController. The smart conversational controller requires an appliction context and is interacted with through the query command. 

### Sample code
```java
  //Import smart conversational client controller
  import com.microsoft.pct.smartconversationalclient.controller;

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



