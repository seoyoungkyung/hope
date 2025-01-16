document.addEventListener("DOMContentLoaded", function() {
    // errorMessage를 data 속성에서 가져오기
    var errorMessage = document.getElementById("error-message").getAttribute("data-error-message");

    // errorMessage가 존재하면 alert로 표시
    if (errorMessage && errorMessage.trim() !== "") {
        alert(errorMessage);
    }

    bindDomEvent();
});


