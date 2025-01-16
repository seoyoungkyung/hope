$(document).ready(function(){
    $("input[name=cartChkBox]").change( function(){
        getOrderTotalPrice();
    });
});

function getOrderTotalPrice(){
    var orderTotalPrice = 0;
    $("input[name=cartChkBox]:checked").each(function() {
        var cartItemId = $(this).val();
        var price = $("#price_" + cartItemId).attr("data-price");
        var count = $("#count_" + cartItemId).val();
        orderTotalPrice += price*count;
    });

    var formattedPrice = orderTotalPrice.toLocaleString();  // 쉼표 추가
    $("#orderTotalPrice").html(formattedPrice+'원');
}

function changeCount(obj){
    var count = obj.value;
    var cartItemId = obj.id.split('_')[1];
    var price = $("#price_" + cartItemId).data("price");
    var totalPrice = count*price;


    $("#totalPrice_" + cartItemId).html(totalPrice+"원");
    getOrderTotalPrice();
    updateCartItemCount(cartItemId, count);
}

function checkAll(){
    if($("#checkall").prop("checked")){
        $("input[name=cartChkBox]").prop("checked",true);
    }else{
        $("input[name=cartChkBox]").prop("checked",false);
    }
    getOrderTotalPrice();
}

function updateCartItemCount(cartItemId, count){
    // var token = $("meta[name='_csrf']").attr("content");
    // var header = $("meta[name='_csrf_header']").attr("content");

    var url = "/cartItem/" + cartItemId+"?count=" + count;

    $.ajax({
        url      : url,
        type     : "PATCH",
        beforeSend : function(xhr){
            /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
            // xhr.setRequestHeader(header, token);
        },
        dataType : "json",
        cache   : false,
        success  : function(result, status){
            console.log("cartItem count update success");
        },
        error : function(jqXHR, status, error){

            if(jqXHR.status == '401'){
                alert('로그인 후 이용해주세요');
                location.href='/members/login';
            } else{
                alert(jqXHR.responseJSON.message);
            }

        }
    });
}

function deleteCartItem(obj) {
    // 아이템 ID 가져오기
    var cartItemId = obj.dataset.id;

    // 확인 창 띄우기
    var confirmDelete = confirm("정말로 삭제하시겠습니까?");

    if (confirmDelete) {
        // 삭제 요청 URL
        var url = "/cartItem/" + cartItemId;

        $.ajax({
            url: url,
            type: "DELETE",
            beforeSend: function(xhr) {
                // CSRF 토큰이 필요하다면 주석 해제하여 추가
                // var token = $("meta[name='_csrf']").attr("content");
                // var header = $("meta[name='_csrf_header']").attr("content");
                // xhr.setRequestHeader(header, token);
            },
            dataType: "json",
            cache: false,
            success: function(result, status) {
                // 삭제 성공 후 페이지 새로 고침 또는 리디렉션
                location.href = '/cart';  // 장바구니 페이지로 리디렉션
            },
            error: function(jqXHR, status, error) {
                // 에러 처리
                if (jqXHR.status == '401') {
                    alert('로그인 후 이용해주세요');
                    location.href = '/members/login';
                } else {
                    alert(jqXHR.responseJSON.message);
                }
            }
        });
    } else {
        // 삭제 취소된 경우
        alert("삭제가 취소되었습니다.");
    }
}

function orders() {
    var selectedItems = getSelectedCartItems(); // 체크된 아이템들의 ID 목록을 가져옴
    if (selectedItems.length === 0) {
        alert("선택된 상품이 없습니다.");
        return;
    }

    // 선택된 아이템들의 ID들을 ','로 구분하여 하나의 문자열로 만듬
    var selectedItemIds = selectedItems.join(","); // 선택된 아이템들의 ID들을 ','로 구분

    // 결제 페이지로 리디렉션하기 전에 확인을 받는 알림 창을 띄움
    var confirmation = confirm("선택한 아이템으로 주문을 진행하시겠습니까?");
    if (confirmation) {
        // 확인을 누르면 결제 페이지로 이동

        var url = "/members/payment"; // 결제 페이지 URL

        // form을 동적으로 생성하여 GET 방식으로 선택된 아이템들을 전달
        var form = document.createElement("form");
        form.method = "GET";  // GET 방식
        form.action = url;

        // 선택된 아이템 ID를 전달할 input 요소 생성
        var input = document.createElement("input");
        input.type = "hidden";
        input.name = "selectedCartItems"; // 서버에서 받을 파라미터 이름
        input.value = selectedItemIds; // 선택된 아이템들의 ID
        form.appendChild(input);

        // 폼을 제출하여 결제 페이지로 이동
        document.body.appendChild(form);
        form.submit();
    } else {
        // 취소를 누르면 아무 동작도 하지 않음
        alert("주문을 취소하였습니다.");
    }
}

function getSelectedCartItems() {
    let selectedItems = [];
    // 체크된 체크박스를 찾아 선택된 cartItemId들을 배열에 담기
    document.querySelectorAll('input[name="cartChkBox"]:checked').forEach(function (checkbox) {
        selectedItems.push(checkbox.value);
    });
    return selectedItems;
}

document.addEventListener("DOMContentLoaded", function() {
    // 모든 가격 요소에 대해 쉼표 추가 처리
    document.querySelectorAll('.price').forEach(function(element) {
        var price = element.innerText.trim().replace("원", "");  // 원화 기호 제거
        var formattedPrice = Number(price).toLocaleString();  // 쉼표 추가
        element.innerText = formattedPrice + "원";  // 쉼표 추가된 가격 + 원화 기호
    });
});

