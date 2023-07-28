package com.kl.grooveo.base.email.service;

import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.kl.grooveo.boundedContext.member.entity.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender javaMailSender;

	public void sendRegistrationEmail(Member member) {

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

		simpleMailMessage.setTo(member.getEmail());

		simpleMailMessage.setSubject("회원가입을 축하드립니다.");

		simpleMailMessage.setText(("""
			안녕하세요 'Grooveo'입니다.
			회원가입을 축하드립니다! %s님! 'Grooveo'에서는 다양한 서비스를 이용하실 수 있습니다.
			지금부터 'Grooveo' 서비스를 자유롭게 이용하실 수 있습니다.
			추가적인 문의사항이 있으시면 언제든지 저희 'Grooveo' 고객센터로 문의해주세요.""").formatted(member.getUsername()));

		javaMailSender.send(simpleMailMessage);
	}

	public void sendUsername(Member member) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

		simpleMailMessage.setTo(member.getEmail());

		simpleMailMessage.setSubject("아이디 찾기");

		simpleMailMessage.setText(("""
			안녕하세요, Grooveo입니다.
			                                
			회원님께서는 아이디 분실을 요청하셨습니다. 아래에 회원님의 아이디를 안내해 드립니다:
			                                
			아이디 : %s
			이메일 주소와 관련된 아이디를 분실하셨을 경우, 이메일 주소를 확인해 보시고 다시 시도해 주세요. 문제가 계속되면 고객 지원팀에 문의해 주세요.
			                                
			감사합니다.""").formatted(member.getUsername()));

		javaMailSender.send(simpleMailMessage);
	}

	private String createCode() {
		Random random = new Random();
		StringBuffer key = new StringBuffer();

		for (int i = 0; i < 8; i++) {
			int index = random.nextInt(4);

			switch (index) {
				case 0:
					key.append((char)(random.nextInt(26) + 97));
					break;
				case 1:
					key.append((char)(random.nextInt(26) + 65));
					break;
				default:
					key.append(random.nextInt(9));
			}
		}
		return key.toString();
	}

	public String sendTemporaryPassword(Member member) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

		simpleMailMessage.setTo(member.getEmail());

		simpleMailMessage.setSubject("임시비밀번호 발급");

		String temporaryPassword = createCode();

		simpleMailMessage.setText(("""
			안녕하세요, 'Grooveo'입니다.
			                
			회원님께서는 비밀번호 분실을 요청하셨습니다. 임시 비밀번호를 안내해 드립니다. 로그인 후에는 반드시 비밀번호를 변경해 주세요.
			                
			임시 비밀번호 : %s
			로그인 후에는 개인정보 보호를 위해 임시 비밀번호를 즉시 변경해 주시기 바랍니다. 비밀번호 변경을 원하지 않으셨거나 문제가 발생하면 고객 지원팀에 문의해 주세요.
			                
			감사합니다.""").formatted(temporaryPassword));

		javaMailSender.send(simpleMailMessage);

		return temporaryPassword;
	}

	public String sendVerificationCode(HttpSession session, String email) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

		simpleMailMessage.setTo(email);

		simpleMailMessage.setSubject("회원가입 이메일 인증");

		String verificationCode = createCode();

		simpleMailMessage.setText(("""
			안녕하세요! 'Grooveo'입니다.
			아래 인증 코드를 사용하여 이메일 인증을 완료해주세요.
			                  
			인증 코드 : %s
			                              
			'Grooveo'에 대한 저희 사이트에서 멋진 음악과 다양한 커뮤니티를 즐겨보세요!
			                
			감사합니다.""").formatted(verificationCode));

		javaMailSender.send(simpleMailMessage);

		session.setAttribute(email, verificationCode);

		return verificationCode;
	}

	public boolean emailCertification(HttpSession session, String userEmail, String inputCode) {
		String verificationCode = (String)session.getAttribute(userEmail);

		if (verificationCode.equals(inputCode)) {
			session.setAttribute("emailVerified", "true");
			return true;
		} else {
			session.setAttribute("emailVerified", "false");
			return false;
		}
	}
}