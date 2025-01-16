document.addEventListener("DOMContentLoaded", function() {
    // 모든 가격 요소에 대해 쉼표 추가 처리
    document.querySelectorAll('.price').forEach(function(element) {
        var price = element.innerText.trim().replace("원", "");  // 원화 기호 제거
        var formattedPrice = Number(price).toLocaleString();  // 쉼표 추가
        element.innerText = formattedPrice + "원";  // 쉼표 추가된 가격 + 원화 기호
    });
});

$(document).ready(function(){

    calculateToalPrice();

    $("#count").change( function(){
        calculateToalPrice();
    });
});

document.addEventListener("DOMContentLoaded", function() {
    // 모든 가격 요소에 대해 쉼표 추가 처리
    document.querySelectorAll('.price').forEach(function(element) {
        var priceText = element.innerText.trim().replace("원", "").replace(/,/g, ""); // 원화 기호와 쉼표 제거

        // 숫자 변환을 시도하고, 실패할 경우 0으로 처리
        var price = parseFloat(priceText);
        if (isNaN(price)) {
            price = 0;  // 유효하지 않은 가격은 0으로 처리
        }

        // 숫자에 쉼표 추가 후 원화 기호 붙여서 표시
        var formattedPrice = price.toLocaleString();
        element.innerText = formattedPrice + "원";  // 쉼표 추가된 가격 + 원화 기호
    });
});


function calculateToalPrice(){
    var count = $("#count").val();
    var price = $("#price").val();
    var totalPrice = price*count;

    var fomettedPrice = totalPrice.toLocaleString();
    $("#totalPrice").html(fomettedPrice + '원');
}

function order(){
    // var token = $("meta[name='_csrf']").attr("content");
    // var header = $("meta[name='_csrf_header']").attr("content");

    var url = "/order";
    var paramData = {
        itemId : $("#itemId").val(),
        count : $("#count").val()
    };

    var param = JSON.stringify(paramData);

    $.ajax({
        url      : url,
        type     : "POST",
        contentType : "application/json",
        data     : param,
        beforeSend : function(xhr){
            /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
            // xhr.setRequestHeader(header, token);
        },
        dataType : "json",
        cache   : false,
        success  : function(result, status){
            alert("주문이 완료 되었습니다.");
            location.href='/';
        },
        error : function(jqXHR, status, error){

            if(jqXHR.status == '401'){
                alert('로그인 후 이용해주세요');
                location.href='/members/login';
            } else{
                alert(jqXHR.responseText);
            }

        }
    });
}



function addCart(){
    // var token = $("meta[name='_csrf']").attr("content");
    // var header = $("meta[name='_csrf_header']").attr("content");

    var url = "/cart";
    var paramData = {
        itemId : $("#itemId").val(),
        count : $("#count").val()
    };

    var param = JSON.stringify(paramData);

    $.ajax({
        url      : url,
        type     : "POST",
        contentType : "application/json",
        data     : param,
        beforeSend : function(xhr){
            /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
            // xhr.setRequestHeader(header, token);
        },
        dataType : "json",
        cache   : false,
        success  : function(result, status){
            alert("상품을 장바구니에 담았습니다.");
            location.href='/cart';
        },
        error : function(jqXHR, status, error){

            if(jqXHR.status == '401'){
                alert('로그인 후 이용해주세요');
                location.href='/members/login';
            } else{
                alert('로그인 후 이용해주세요');
                location.href='/members/login';
            }

        }
    });
}

$(document).ready(function(){
    $(".delivery-content").hide();

    $(".delivery-title-box").click(function(){
        // .delivery-content를 토글
        $(".delivery-content").toggle();

        // .down_img를 클릭할 때마다 180도씩 회전
        $(".down_img img").toggleClass("rotate");

        // 회전 각도를 180도씩 증가시킴
        var currentRotation = $(this).data('rotation') || 0;
        currentRotation += 180;
        $(this).data('rotation', currentRotation);
        $(".down_img img").css('transform', 'rotate(' + currentRotation + 'deg)');
    });
});


$(document).ready(function() {
    var errorMessage = $('#errorMessage').data('error-message');
    if (errorMessage) {
        alert(errorMessage);
    }

    bindDomEvent();
});

function bindDomEvent(){
    $(".custom-file-input").on("change", function() {
        var fileName = $(this).val().split("\\").pop();  //이미지 파일명
        var fileExt = fileName.substring(fileName.lastIndexOf(".")+1); // 확장자 추출
        fileExt = fileExt.toLowerCase(); //소문자 변환

        if(fileExt != "jpg" && fileExt != "jpeg" && fileExt != "gif" && fileExt != "png" && fileExt != "bmp"){
            alert("이미지 파일만 등록이 가능합니다.");
            return;
        }

        $(this).siblings(".custom-file-label").html(fileName);
    });
}


$(document).ready(function(){
    $("#searchBtn").on("click",function(e) {
        e.preventDefault();
        page(0);
    });
});

function page(page){
    var searchDateType = $("#searchDateType").val();
    var searchSellStatus = $("#searchSellStatus").val();
    var searchBy = $("#searchBy").val();
    var searchQuery = $("#searchQuery").val();

    location.href="/admin/items/" + page + "?searchDateType=" + searchDateType
        + "&searchSellStatus=" + searchSellStatus
        + "&searchBy=" + searchBy
        + "&searchQuery=" + searchQuery;
}
