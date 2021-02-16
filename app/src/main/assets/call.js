let localVideo = document.getElementById("local-video")  //비디오 엘리멘트 가져오기
let remoteVideo = document.getElementById("remote-video")

//연결이 안되어있음, 비디오가 플레이가 안된다면 opacity==0 //그냥 디폴트 값인듯
localVideo.style.opacity = 0
remoteVideo.style.opacity = 0

//비디오 연결이 되고 플레이가 되면 오퍼시티 == 1
localVideo.onplaying = () => { localVideo.style.opacity = 1 }  //프라이머리
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 } //세컨더리

let peer //잘은 모르겠지만 peerjs.js파일에 존재하는 녀석..!
// 웹의 콘솔에서 우리가 init("A")로 입력하면 peerjs서버에서 클라이언트 연결되었다고 할때 그것!
function init(userId) {
    peer = new Peer(userId, {
        host: '172.30.1.14',
        port: 9000,
        path: '/videocallapp'
    })

    peer.on('open', () => {
        Android.onPeerConnected()
    })

    listen()
}

let localStream
// 비디오, 오디오 허용해주고 가져오는 녀석
function listen() {
    peer.on('call', (call) => {
        // 비디오 오디오 쓰겠어!
        navigator.getUserMedia({
            audio: true, 
            video: true
        }, (stream) => { // callbackfun이 주는 스트림림
            localVideo.srcObject = stream
            localStream = stream

            //답장
            call.answer(stream)
            call.on('stream', (remoteStream) => {
                remoteVideo.srcObject = remoteStream

                remoteVideo.className = "primary-video"
                localVideo.className = "secondary-video"

            })

        })
        
    })
}

function startCall(otherUserId) { // 내가 전화할 다른 사람의 아이디
    navigator.getUserMedia({
        audio: true,
        video: true
    }, (stream) => {

        localVideo.srcObject = stream
        localStream = stream

        const call = peer.call(otherUserId, stream)
        call.on('stream', (remoteStream) => {
            remoteVideo.srcObject = remoteStream

            remoteVideo.className = "primary-video"
            localVideo.className = "secondary-video"
        })

    })
}

//안드로이드에서 가져오는 녀석인가? 아직은 잘몰랑
function toggleVideo(b) { // b==boolean
    if (b == "true") {
        localStream.getVideoTracks()[0].enabled = true
    } else {
        localStream.getVideoTracks()[0].enabled = false
    }
} 

function toggleAudio(b) {
    if (b == "true") {
        localStream.getAudioTracks()[0].enabled = true
    } else {
        localStream.getAudioTracks()[0].enabled = false
    }
} 