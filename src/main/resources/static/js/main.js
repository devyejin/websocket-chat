'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];
//roomId param 가져오기
let url = new URL(location.href).searchParams;
let roomId = url.get('roomId');

//socket 통신 연결!
function connect(event) {
    console.log("채팅방 입장 버튼 클릭!!");

    username = document.querySelector('#name').value.trim();

    //username 중복 체크
    //비동기로 응답 받아온 후 밑에 로직 처리되야하므로 Promise로 수정
    // try {
    //     await isDuplicateName();
    // } catch(error) {
    //     console.error("Occur username error : ", error);
    //
    // }

    isDuplicateName();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        let socket = new SockJS('/ws-stomp'); //소켓 통신 시작
        stompClient = Stomp.over(socket); //WebSocket 연결을 통해 Stomp 클라이언트를 생성

        //stompClient.connect((빈 객체 or연결 중 추가 정보 전달), 연결성공콜백함수, 연결오류콜백함수)
        stompClient.connect({}, onConnected, onError);
    }

    event.preventDefault(); //form전송 이벤트시, 새로고침 발생해서 방지 -> 웹 소켓 연결 유지
}

function isDuplicateName() {

    console.log(roomId);
    // return new Promise((resolve, reject) => {
        //비동기로 체크
        $.ajax({
            type: "GET",
            url: "/chat/duplicate-username",
            data: {
                "username":username,
                "roomId":roomId
            },
            success: (data) => {
                console.log("data={}", data);
                username = data; //중복이면, 서버에서 숫자붙여서 반환
                // resolve(); // 성공 처리
            }
            // error: (error) => {
            //     reject(error); // Promise를 에러 상태로 변경
            // }
        })
    // })
}


function onConnected() {
    console.log(roomId);
    // 구독(sub,topic)할 url => /sub/chat/room/{roomId} 구독 시작
    stompClient.subscribe('/sub/chat/room/'+roomId, onMessageReceived);

    // 발행(pub, app), 서버에 username 가진 유저가 들어왔다고 알림
    // Tell your username to the server
    //app/chat.addUser로 알림
    stompClient.send("/pub/chat/enterUser",
                    {},
                    JSON.stringify({
                        sender: username,
                        type: 'JOIN',
                        "roomId": roomId})
                    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

//유저 리스트 받기
//ajax로 유저 리스트를 받으며 유저 입장/퇴장 문구 나올때마다 실행
function getUserList() {
    //id가 list인 요소를 가져오기 (jQuery)
    const $list = $("#list");

    $.ajax({
        type:"GET",
        url:"/chat/user-list",
        data: {
            "roomId":roomId
        },
        success: (data) => {
            let users = "";
            for(let i=0; i<data.length; i++) {
                users += "<li class='dropdown-item'>" + data[i] + "</li>"
            }
            //list 요소에 첨가
            $list.html(users);
        }
    })

}


// JSON형식으로 서버로 전송
function sendMessage(event) {
    console.log("click message send butten!!!");

    let messageContent = messageInput.value.trim();

    //입력 메시지가 있고(true) && webSocket연결이 성공해서 stomp객체가 있는 경우만 메시지 전송
    if(messageContent && stompClient) {
        let chatMessage = {
            "roomId" : roomId,
            message: messageInput.value,
            sender: username,
            type: 'CHAT'
        };
        //stomp를 이용해서 서버로 메시지 전송, 발행(pub,app)
        stompClient.send("/pub/chat/sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = ''; //입력 필드 초기화
    }
    event.preventDefault();
}

//서버로부터 메시지 받음
function onMessageReceived(payload) {

    console.log("received message from the server!!")
    let chat = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(chat.type === 'JOIN') {
        messageElement.classList.add('event-message');
        chat.content = chat.sender + chat.message;
        getUserList();
        console.log("join display");

    } else if (chat.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        chat.content = chat.sender + chat.message;
        getUserList();
        console.log(("leave display"));

    } else {
        console.log("received chat-message")
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(chat.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(chat.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(chat.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(chat.message);
    textElement.appendChild(messageText);

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


//사용자 이름 입력 후 submit 이벤트 발생시 connect 함수 호출
usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)