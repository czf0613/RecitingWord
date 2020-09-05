# RecitingWord
An Android app that help you recite words while you lit up the screen. Like 百词斩 and stuff.



## 1. Project Background

Our application is just a simple software which aims at helping students to remember English word and go over. Now there existed a number of English learning apps like 有道词典, 百词斩, 扇贝英语 and stuff like that, so is it a duplicate of those kind of apps? Obviously, it’s not. We are trying to help people manage their fragment time and use that to memorize words and go over them. 

Our application has 3 main activities. The first one is “Test”, the second one is “Review”, the last one is “Word List”. While you are doing tests, you make a mistake and the very word will be sent to Review database and Word List database. These two databases are used to store your wrong words and the words you don’t know. 

After you have done several tests, you can enter the main page to see how many words you have done, and also, we provide you a page to go over the words. You can see the tests you have done today and review the mistakes in case that you fall again. It’s pretty useful.

World List is for you to memory some words that you don’t know yet. Seeing these words and you can get familiar with them, finally you can master all of them. This is just the procedure we learn English, isn’t it?



## 2. Project Design Concept

Our core design concept is to help the current population get rid of mobile phone addiction, or "once you pick up your phone, you don't know what you are doing". Many people now have word-memorizing apps on their phones, but they don't actually use them. Our APP adds the words that users need to remember to the lock screen of the mobile phone. However, different from memorizing words on the lock screen, our APP requires customers to answer the specified number of words correctly to unlock the mobile phone, the number of which can be set by the user. Based on this, we chose this APP as our design project to help users remember words in their own fragments.

It’s reported that we pick up our phones over 60 times a day, so it’s very reasonable to take this bunch of time in use. We order users to answer 2 question before you unlock your phone. We are sure that it won’t take up you for a long time, I promise. But at least you can ‘enjoy’ 100 words a day, sounds interesting.

I have already noticed that many students had installed a lot of English learning apps in their phones but hardly had them run. I admit that it’s a little bit kind of forcing you to learn English, but it really work, at least you spend more time in remembering those words. You know, time pays off.



## 3. Process discussion

First, we built the main framework of the project. The main interface we need is the main interface of the software and the lock screen.

First, I introduce the lock screen interface, which is the most common interface for users and is hosted in MainActivity.First, define the MainActivity class, which is inherited from AppCompatActivity, and USES the interfaces of onClickListener, RadioGroup, OnCheckedChangeListener, SynathesizerListener, which is used to monitor the keys and serve the following operations. Declare the required main components in the class and set them to private properties to maintain their integrity and avoid malicious modification.It mainly includes 1timeText, dateText, wordText and englishText of TextView type, which are used to display words and phonetic symbols respectively.Then, mMonth, mDay, mWay and other strings of String class are defined to display the time.In the words section we present the RadioButton type to load the word options, which are used to provide the user with options in the lock screen interface.Then define a lightweight database called sharePreferences to store and edit data such as cet-4 and cet-6 words to be used in the software, where you can determine the number of questions to be answered and record the correct ones.

Now that we've set up the basic components, let's take a quick look at the functions in MainActivity.

First, the contents of the lock screen are displayed at the top of the mobile phone screen in onCreate(). This is to display the Activity on the lock screen and call the initialization function of the lightweight database function.

In the initialization of the database we define two parameters, the first to specify the name of the file and the second to specify the mode of operation of the file.We use ArrayList to add ten Random Numbers up to 20, to prepare for the Random appearance of the following words, and to process the Numbers here with Random.The database initialization just call once, the first thing we pass AssetsDatabaseManager. InitManager obtaining management object, because the database through the management object is needed to obtain, the database again.

Next we override the onStart() function to set the time.Our core job in this function is to get the system time and set it to display.We get the Calendar object through getInstance and further get the current time of the system, and set the expressions of the month and date in detail and write them in Chinese.For dates, we also set a Boolean function called isToday() to check.

We achieve the result of the answer through btnGetText judgment as well as the corresponding text color Settings, through setTextColor sets the right situation to green, wrong set of red, and record the title of right and wrong number saved to the database, will be in the wrong topic Id into sharePreferences so as to realize the record of the wrong topic, to see the error in the subsequent wrong topic this function words, achieve the goal of many times to review.

Next, reload the onTouchEvent function to set the desired effect when your finger touches the screen.Here we set up a simple animation, the final effect of the slide marked as mastered, right slide down a problem.

This is the main implementation of MainActivity.

Next, we briefly introduce the implementation of the main interface. We write the main interface of the software in the homeactivity.java file. In this class, we bind the interface to monitor the status of the mobile phone screen, define the interface for review and setting, bind the review interface and set the interface, and define the error button and the translation button at the same time.This completes the basic component setup.

In the initialization, we first set the ScreenListener to monitor the screen, and then reload the onScreenOn() function to determine whether the lock screen button is turned on in the Settings screen. If so, the word lock screen screen, namely MainActivity mentioned above, is started. The function sharePreferences is used to determine whether the screen is unlocked and give corresponding feedback.

If the phone is already locked, go to the onScreenOff() function and change the tf field in the database to true; otherwise, go to the onUserPresent() function and set the tf field in the database to false.

The next step is to make a transaction for fragments through setFragment; Then two functions, study and set, are used to jump to the interface, corresponding to the review interface and the setting interface.

 

Additionally, to make fragments into function, we need several adapters to help with. About the adapters, we need to implement the classes to make the usage of the interface efficient.In the file CardFragmentPagerAdapter.java,class CardFragmentPagerAdapter extends FragmentStatePagerAdapter. There is a private member data named wrongData with the type of List storing the words on which you have just made mistakes.In the construct method, we simply set the List with the given data. Then we override getItem method, which enables CardFragment to have a new instance with the given position of the wrong word.The getCount method counts the words you have made mistakes, and it is the number of words stored in the wrongData list. In the file ReviewFragmentPagerAdapter.java, class ReviewFragmentPagerAdapter extends FragmentStatePagerAdapter. Similar to the CardFragmentPagerAdapter, there is a private member data named reviewData with the type of List storing the words on which you have just learnt. In the construct method, the given data list is set into the private data list. Then we override getItem method, which enables ReviewFragment to have a new instance with the given position of the  review words .The getCount method counts the words you have learnt, and it is the number of words stored in the reviewData list.



## 4.Analyze

Running Time Screenshots and codes are as follows.

### 1,  When you unlock the phone, the question will be presented.

You can swap down to skip, and swap left to answer next question.

<img src="https://i.loli.net/2020/09/05/VeMhNnJz7mEXsax.png" alt="image-20200905112942234" style="zoom:200%;" />

If you select the correct answer, its color will be green. If not, its color will be red.

<img src="https://i.loli.net/2020/09/05/lNqMteYiVJ7Ofro.png" alt="image-20200905113042954" style="zoom:200%;" />

### 2, 

When you open the APP directly, the REVIEW screen will show the general situation of the behaviors on your phone such the number of wrong words, unlock times, the level of the words and so on. There is also a quote with English and Chinese on the top of the screen and it will change by the time. The words lists button refer to the next screen of the new words that you had made mistakes on. You can directly press the START REVIEWING button to learn all the words that you have met again. SETUP refer to the setting stuffs.

<img src="https://i.loli.net/2020/09/05/QFPIYhjN92qxUpr.png" alt="image-20200905113126518" style="zoom:200%;" />

### 3, The WORDS LISTS screen refer to the word that you made mistakes on. You can press GOT to set that you have got it.

<img src="https://i.loli.net/2020/09/05/9j1MZsyKtpAaIrd.png" alt="image-20200905113156958" style="zoom:200%;" />

If you have got all the words, it will present GOOD JOB as below.

<img src="https://i.loli.net/2020/09/05/yhKMDi8GFE9HkI1.png" alt="image-20200905113221102" style="zoom:200%;" />

### 4, 

In the Review part, you can go through all the words you learn today, and you can choose REMEMBER or NOT YET. The change of cards is implemented by the CardTransformer.

<img src="https://i.loli.net/2020/09/05/foLQ4D2rGph6wgB.png" alt="image-20200905113302273" style="zoom:200%;" />

### 5, 

In the SETUP screen, you can choose to turn on the function of the APP. You can setup the level and the number of words each time you unlock the phone. The new words that you want learn every day and the words that you want to review every day can also be set.

You need to choose a Wallpaper or there is an existing wallpaper in the direct of QQ file_recv. You had better choose you own wallpaper.

<img src="https://i.loli.net/2020/09/05/bQsGFmTDiIrwYzn.png" alt="image-20200905113420903" style="zoom:200%;" />