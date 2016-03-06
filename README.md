# ChatMongoDB
This is the chat demo which uses mongodb cursor to pull all the messages

Notes:

* [x] Please check the library I added in the lib folder it's wrapper around mongo java driver to get rid of ssl which is required by heroku
* [x] Make sure to create db.createCollection( "chat", { capped: true, size: 100000 }) and insert some item


** Video **
<img src='https://github.com/anujacharya1/ChatMongoDB/blob/master/rec_chat.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />
