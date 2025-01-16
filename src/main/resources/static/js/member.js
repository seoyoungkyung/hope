$(document).ready(function() {

    // 고객센터 클릭 시 전화번호 보여주기
    $('#customerServiceText, #serviceImage').on('click', function() {
        $('#phoneNumber').toggle();  // 전화번호의 보이기/숨기기 토글
    });

    // 폼 제출 처리
    $('.m_form').on('submit', function(event) {
        event.preventDefault(); // 기본 폼 제출을 막음

        // 폼이 유효한지 확인
        if (this.checkValidity()) {
            // AJAX 요청으로 데이터 전송
            $.ajax({
                type: 'POST',
                url: '/members/modify', // 수정 요청을 보내는 URL
                data: $(this).serialize(), // 폼 데이터를 직렬화하여 전송
                success: function(response) {
                    // 수정 완료 메시지를 보여주고 리다이렉트
                    alert('수정이 완료되었습니다.');
                    window.location.href = '/members/myPage'; // 원하는 페이지로 리다이렉트
                },
                error: function(xhr, status, error) {
                    // 오류 처리
                    alert('비밀번호를 확인해주세요.');
                }
            });
        } else {
            // 폼이 유효하지 않은 경우
            alert('입력한 정보를 확인해 주세요.');
        }
    });

});

function showMessage() {
    // 회원가입 완료 메시지
    alert("회원가입이 완료되었습니다. \n로그인을 해주세요.");
}

function showDeleteMessage() {
    // 회원가입 완료 메시지
    alert("회원을 탈퇴합니다.");
}
