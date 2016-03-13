# MonSub

MonSub is project which allows Android client to setup a chat functionality in few lines of code. It basically uses the capped collection
abilities of Mongo and have a cursor act as message notifcation.

A demo application is provided as the sample which uses monsub interface.

## How to use this library:

1. Copy com.anuj.monsub package into your project
2. Copy the mongo-java-driver-3.2.0-SNAPHOT.jar into your lib directory
3. Update you gradle file to import that jar, look at the sample for example
4. Create heroku account and start-up the instance
5. Heroku come up with free 2GB of mongoLab, use that for your testing
6. You would need database name and URL for mongo
7. The sess1 and sess2 is the sample can be anything specify to your application, think as if unique
    name between the two clients

## API:

register(): register your unique name with the mongo

open() : open will give you any message receiving on the channel

send(): this is used to send the messgae on the channel, which inturn will be
        publish to different clients



## Future Enhancement:

1. close functionality which will close all the ongoing connection for the client
2. make use of generic instead of text to send the message to other client

Notes:

* [x] Please check the library I added in the lib folder it's wrapper around mongo java driver to get rid of ssl which is required by heroku

## Video

<img src='https://github.com/anujacharya1/ChatMongoDB/blob/master/rec_chat.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />
