// 페이지 번호를 data-page에서 읽어서 사용
function cancelOrder(orderId) {
    var url = "/order/" + orderId + "/cancel";
    var paramData = { orderId: orderId };
    var param = JSON.stringify(paramData);

    $.ajax({
        url: url,
        type: "POST",
        contentType: "application/json",
        data: param,
        dataType: "json",
        cache: false,
        success: function(result, status) {
            alert("주문이 취소되었습니다.");

            // 페이지 번호 읽기 (data-page에서 페이지 번호 가져오기)
            var page = document.getElementById("pageContainer").getAttribute("data-page");

            // 페이지 번호가 없으면 기본값으로 처리 (0으로 설정)
            if (page === null || page === undefined || isNaN(page)) {
                console.error("페이지 번호가 유효하지 않습니다.");
                page = 0; // 기본값 설정
            }

            // 페이지 번호가 유효하면 주문 목록으로 리다이렉트
            location.href = '/orders/' + page;
        },
        error: function(jqXHR, status, error) {
            if (jqXHR.status == '401') {
                alert('로그인 후 이용해주세요');
                location.href = '/members/login';
            } else {
                alert(jqXHR.responseText);
            }
        }
    });
}

document.addEventListener("DOMContentLoaded", function() {
    // 모든 가격 요소에 대해 쉼표 추가 처리
    document.querySelectorAll('.price').forEach(function(element) {
        var price = element.innerText.trim().replace("원", "");  // 원화 기호 제거
        var formattedPrice = Number(price).toLocaleString();  // 쉼표 추가
        element.innerText = formattedPrice + "원";  // 쉼표 추가된 가격 + 원화 기호
    });
});