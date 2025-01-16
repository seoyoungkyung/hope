document.addEventListener("DOMContentLoaded", function () {
    console.log("js..연동시작");

    // 가격을 포맷팅하는 함수
    function formatPrice(price) {
        return Number(price).toLocaleString() + "원"; // 쉼표 추가하고 '원' 붙임
    }

    // 안전하게 숫자를 변환하는 함수
    function safeParseInt(value, defaultValue = 0) {
        let parsedValue = parseInt(value.replace("원", "").trim().replace(",", ""), 10);
        return isNaN(parsedValue) ? defaultValue : parsedValue; // 변환 실패 시 기본값 반환
    }

    // 가격에 쉼표를 추가하는 공통 함수
    function updatePrices(selector) {
        document.querySelectorAll(selector).forEach(function (element) {
            let price = safeParseInt(element.innerText); // 안전한 숫자 변환
            element.innerText = formatPrice(price); // 쉼표 추가 후 '원' 붙여서 표시
        });
    }

    // 주문 금액 합계를 갱신
    function getOrderTotalPrice() {
        let totalPrice = 0;

        // 각 상품의 총 금액을 더함
        document.querySelectorAll('[id^="totalPrice_"]').forEach(function (element) {
            let price = safeParseInt(element.innerText); // 안전한 숫자 변환
            totalPrice += price;
        });

        // `final_pay` 요소에서 현재 값 가져오기
        let finalPayElement = document.getElementById("final_pay");
        if (finalPayElement) {
            let finalPayPrice = safeParseInt(finalPayElement.innerText); // 안전한 숫자 변환
            totalPrice += finalPayPrice; // final_pay 금액을 총합에 더함
        }

        // 전체 주문 금액에 쉼표 추가 후 `orderTotalPrice`에 반영
        let orderTotalPriceElement = document.getElementById("orderTotalPrice");
        if (orderTotalPriceElement) {
            orderTotalPriceElement.innerText = formatPrice(totalPrice); // '원' 붙여서 표시
        }
        return totalPrice;
    }

    // 페이지 로딩 시 바로 가격과 총 주문 금액을 갱신
    updatePrices(".price"); // 각 상품의 가격에 쉼표 추가
    updatePrices('[id^="totalPrice_"]'); // 각 상품의 총 금액에 쉼표 추가
    let totalPrice = getOrderTotalPrice(); // 총 주문 금액 계산

    // HTML 요소에서 메시지 속성 값을 가져옴
    var messageElement = document.getElementById("message");
    if (messageElement) {
        var successMessage = messageElement.getAttribute("data-success-message");
        var errorMessage = messageElement.getAttribute("data-error-message");

        // 성공 메시지가 있으면 alert 띄우기
        if (successMessage) {
            alert(successMessage);
        }

        // 오류 메시지가 있으면 alert 띄우기
        if (errorMessage) {
            alert(errorMessage);
        }
    }

    // 결제 관련 변수
    var IMP = window.IMP;
    IMP.init("imp55425362");
    var merchantUid = "merchant_" + new Date().getTime();
    var buyerEmail = document.getElementById('buyerEmail').value;
    var buyerName = document.getElementById('buyerName').value;
    var buyerTel = document.getElementById('buyerTel').value;
    var buyerAdd = document.getElementById('buyerAdd').value;

    // 카카오페이 결제 함수 정의
    window.selectKakaoPay = function() {
        IMP.request_pay({
            pg: "kakaopay.TC0ONETIME",
            pay_method: "card",
            merchant_uid: merchantUid,
            name: "RECORD SHOP",
            amount: totalPrice,
            buyer_email: buyerEmail,
            buyer_name: buyerName,
            buyer_tel: buyerTel,
            buyer_add: buyerAdd
        }, function (rsp) {
            $.ajax({
                type: "POST",
                url: "/verifyIamPort/" + rsp.imp_uid
            }).done(function (data) {
                if (rsp.paid_amount == data.response.amount) {
                    alert("결제 성공");
                    orders();
                } else {
                    alert("결제 실패");
                }
            });
        });
    };

    // 이니시스 결제 함수 정의
    window.selectInicis = function() {
        IMP.request_pay({
            pg: "html5_inicis.INIpayTest",
            pay_method: "card",
            merchant_uid: merchantUid,
            name: "RECORD SHOP",
            amount: totalPrice,
            buyer_email: buyerEmail,
            buyer_name: buyerName,
            buyer_tel: buyerTel,
            buyer_add: buyerAdd
        }, function (rsp) {
            $.ajax({
                type: "POST",
                url: "/verifyIamPort/" + rsp.imp_uid
            }).done(function (data) {
                if (rsp.paid_amount == data.response.amount) {
                    alert("결제 성공");
                    orders();
                } else {
                    alert("결제 실패");
                }
            });
        });
    };

    var paymentBtn = document.getElementById("paymentBtn");

    paymentBtn.addEventListener("click", function() {
        // 결제 방법 선택
        var selectedPaymentMethod = null;

        if (document.getElementById("kakaoPayImg").classList.contains("selected")) {
            selectedPaymentMethod = "kakaoPay";
        } else if (document.getElementById("inicisPayImg").classList.contains("selected")) {
            selectedPaymentMethod = "inicisPay";
        }

        // 결제 방식에 맞는 결제 함수 호출
        if (selectedPaymentMethod === "kakaoPay") {
            selectKakaoPay();
        } else if (selectedPaymentMethod === "inicisPay") {
            selectInicis();
        } else {
            alert("결제 방법을 선택해 주세요.");
        }
    });

    // 결제 방법 선택 시 선택된 이미지에 클래스 추가/제거
    document.getElementById("kakaoPayImg").addEventListener("click", function() {
        document.getElementById("kakaoPayImg").classList.add("selected");
        document.getElementById("inicisPayImg").classList.remove("selected");
    });

    document.getElementById("inicisPayImg").addEventListener("click", function() {
        document.getElementById("inicisPayImg").classList.add("selected");
        document.getElementById("kakaoPayImg").classList.remove("selected");
    });

    function orders() {
        var url = "/item/payment";  // 결제 처리 URL

        var dataList = [];  // cartOrderDtoList에 담을 주문 아이템들을 위한 배열
        var paramData = {};  // 최종 데이터 객체

        // 이미 체크된 아이템들의 cartItemId 값을 가져옴 (selectedCartItems로 전달된 값)
        var selectedCartItems = document.getElementById("selectedCartItems").value;
        console.log("selectedCartItems : " + selectedCartItems);

        if (selectedCartItems) {
            // selectedCartItems를 ','로 구분하여 배열로 변환
            var selectedItemIds = selectedCartItems.split(",");

            // 각 selectedCartItems에 대해 CartOrderDto 객체를 만들어서 dataList에 추가
            selectedItemIds.forEach(function(cartItemId) {
                var data = { "cartItemId": cartItemId };  // CartOrderDto 형태로 데이터 생성
                dataList.push(data);  // dataList에 추가
            });
        }

        paramData['cartOrderDtoList'] = dataList;  // cartOrderDtoList에 데이터 추가

        console.log("전송할 데이터: ", JSON.stringify(paramData));

        var param = JSON.stringify(paramData);  // JSON 형식으로 변환

        $.ajax({
            url: url,
            type: "POST",
            contentType: "application/json",
            data: param,
            dataType: "json",
            cache: false,
            success: function(result) {
                alert("주문이 완료되었습니다.");
                location.href = '/orders';  // 주문 완료 후 주문 목록 페이지로 이동
            },
            error: function(jqXHR) {
                if (jqXHR.status == '401') {
                    alert('로그인 후 이용해주세요');
                    location.href = '/members/login';  // 로그인 페이지로 이동
                } else {
                    var errorMessage = jqXHR.responseJSON ? jqXHR.responseJSON.message : jqXHR.responseText;
                    alert(errorMessage);  // 오류 메시지 표시
                }
            }
        });
    }

});

document.getElementById('kakaoPayImg').addEventListener('click', function() {
    this.classList.toggle('selected');
});
