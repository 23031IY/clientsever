// app.js
// ★ 接続先は必ず自分のサーバに合わせて変更
export const CLIENT_SERVER_WS = "ws://192.168.56.1:8080/ChinchiroServer/ws";

let ws = null;

export function connect(onMessage) {
    ws = new WebSocket(CLIENT_SERVER_WS);

    ws.onopen = () => {
        console.log("[WS] connected");
    };

    ws.onmessage = (e) => {
        console.log("[WS] recv:", e.data);
        onMessage(e.data);
    };

    ws.onclose = () => {
        console.log("[WS] closed");
    };

    ws.onerror = (e) => {
        console.error("[WS] error", e);
    };
}

export function send(obj) {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        alert("サーバに接続できていません");
        return;
    }
    ws.send(JSON.stringify(obj));
}

export function showMessage(id, msg) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = msg;
    el.style.display = "block";
}
