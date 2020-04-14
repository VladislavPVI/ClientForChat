var stompClient = null;
var messageInput = document.querySelector('#message');
var username = document.getElementById("author");
var messageArea = document.querySelector('#greetings');

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/chat/websocketChat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', onMessageReceived);
        stompClient.send("/app/addUser", {}, JSON.stringify({name: username.textContent, type: 'JOIN'}))

    });


}

function onMessageReceived(chatmessage) {

    var message = JSON.parse(chatmessage.body);
    var messageElement = document.createElement('li');
    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.text = message.name + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.text = message.name + ' left!';
    } else {
        messageElement.classList.add('chat-message');
        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.name[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.name);
        messageElement.appendChild(avatarElement);
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.name);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);


        var usernameTime = document.createElement('span');
        usernameTime.className = "small text-muted";
        var usernameTextTime = document.createTextNode(" "+message.time);

        usernameTime.appendChild(usernameTextTime);
        messageElement.appendChild(usernameTime);

    }
    var textElement = document.createElement('p');
    if(message.type === 'LINK') {
        var a = document.createElement('a');
        var linkText = document.createTextNode("Random article");
        a.appendChild(linkText);
        a.href = message.text;
        a.target="_blank";

        textElement.appendChild(a);
    }
    else {
        var messageText = document.createTextNode(message.text);
        textElement.appendChild(messageText);

    }

    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;

}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
if (messageInput.value) {
    var chatMessage = {
        name: username.textContent,
        text: messageInput.value,
        type: 'CHAT'
    };
    stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
    messageInput.value = '';
}
}


$(function () {
    //$("form").on('submit', function (e) {
    //      e.preventDefault();
    //  });
    //$('body').on('submit', '#connect', function(e) { connect(); });

    connect();
    $( "#connect" ).click(function(e) { connect();  e.preventDefault();});
    $( "#disconnect" ).click(function(e) { disconnect(); e.preventDefault();});
    $( "#send" ).click(function(e) { sendName(); e.preventDefault(); });
});
