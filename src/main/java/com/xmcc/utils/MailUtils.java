package com.xmcc.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发邮件工具类
 */
public final class MailUtils {
    private static final String USER = "xczhangxinglin@163.com"; // 发件人称号，同邮箱地址
    private static final String PASSWORD = "970914zxl"; // 如果是qq邮箱可以使户端授权码，或者登录密码

    /**
     *
     * @param to 收件人邮箱
     * @param text 邮件正文
     * @param title 标题
     *
    /* 发送验证信息的邮件 */
    public static boolean sendMail(String to, String text, String title){
        try {
            final Properties props = new Properties();
            props.put("userName", "xczhangxinglin@163.com");//因新注册邮箱会出现 554 DT:STM(发送的邮件内容包含了未被许可的信息，或被系统识别为垃圾邮件.)所以需要将要发送邮件先抄送一份，这个是设置抄送用户名
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.163.com");
//            props.put("userName", "xczhangxinglin@163.com");//设置抄送人
            // 发件人的账号
            props.put("mail.user", USER);
            //发件人的密码
            props.put("mail.password", PASSWORD);

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);

            //防止成为垃圾邮件，披上outlook的马甲
            // message.addHeader("X-Mailer","Microsoft Outlook Express 6.00.2900.2869");

            // 设置发件人
            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress(username);
            message.setFrom(form);

            message.setRecipients(javax.mail.Message.RecipientType.CC,
                    InternetAddress.parse(""));
            message.addRecipients(MimeMessage.RecipientType.CC,
                    InternetAddress.parse(props.getProperty("userName")));//抄送邮件

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(text,"text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
