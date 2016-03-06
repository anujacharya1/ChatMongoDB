# ChatMongoDB
This is the chat demo which uses mongodb cursor to pull all the messages

Notes:

[*] Please check the library I added in the lib folder it's wrapper around mongo java driver to get rid of ssl which is required by heroku
[*] Make sure to create db.createCollection( "chat", { capped: true, size: 100000 }) and insert some item
