$(document).ready(function() {

    // 이전 버튼 클릭 (앨범 이벤트 배너)
    $('.button-prev').click(function(){
        $('.long_img:last').prependTo('.long_box');
        $('.long_box').css('margin-left', -1200);
        $('.long_box').stop().animate({marginLeft: 0}, 800);

    });

    // 다음 버튼 클릭
    $('.button-next').click(function(){
        $('.long_box').stop().animate({marginLeft: -1200}, 800, function() {
            $('.long_img:first').appendTo('.long_box');
            $('.long_box').css('margin-left', 0);
        });
    });

    //item-detail 슬라이드
    $('.item-detail').each(function() {
        var text = $(this).text();         // 텍스트 내용 가져오기
        var maxLength = 50;               // 텍스트 최대 길이
        var minDuration = 10;              // 최소 애니메이션 시간 (기본 속도)
        var maxDuration = 20;              // 최대 애니메이션 시간 (긴 텍스트에 대해서 속도 느리게)

        if (text.length > maxLength) {
            // 텍스트의 길이에 맞게 애니메이션 시간을 설정 (길이가 길수록 애니메이션이 느려짐)
            var animationDuration = Math.min(minDuration + (text.length / maxLength) * 15, maxDuration);
            // 동적으로 애니메이션 속도 설정
            $(this).find('.slide-text').css('animation-duration', animationDuration + 's');
        }
    });

});

document.addEventListener("DOMContentLoaded", function() {
    // 모든 가격 요소에 대해 쉼표 추가 처리
    document.querySelectorAll('.price').forEach(function(element) {
        var price = element.innerText.trim().replace("원", "");  // 원화 기호 제거
        var formattedPrice = Number(price).toLocaleString();  // 쉼표 추가
        element.innerText = formattedPrice + "원";  // 쉼표 추가된 가격 + 원화 기호
    });
});

