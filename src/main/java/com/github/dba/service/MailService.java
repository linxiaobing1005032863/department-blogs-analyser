package com.github.dba.service;

import com.github.dba.model.Blog;
import com.google.common.collect.Maps;
import com.sina.sae.mail.SaeMail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class MailService {
    private static final Log log = LogFactory.getLog(MailService.class);

    @Value("${send_mail}")
    private String sendMail;

    @Value("${mail_password}")
    private String mailPassword;

    @Value("${smtp_host}")
    private String smtpHost;

    @Value("${smtp_port}")
    private int smtpPort;

    @Value("${to_mail}")
    private String toMail;

    @Resource(name = "velocityConfigurer")
    private VelocityConfigurer velocityConfigurer;

    public void sendNewBlogs(List<Blog> blogs) {
        log.debug("send new blogs mail start");
        log.debug(String.format("blogs: %s", blogs));
        SaeMail mail = new SaeMail();

        mail.setFrom(sendMail);
        mail.setSmtpUsername(sendMail);
        mail.setSmtpPassword(mailPassword);
        mail.setSmtpHost(smtpHost);
        mail.setSmtpPort(smtpPort);

        mail.setTo(new String[]{toMail});
        mail.setSubject("[通知]有人发文章啦~大家速顶!");
        mail.setContentType("HTML");
        mail.setChartset("UTF-8");

        Map<String, Object> model = Maps.newHashMap();
        model.put("blogs", blogs);

        String content = VelocityEngineUtils.mergeTemplateIntoString(
                velocityConfigurer.getVelocityEngine(), "new_blog_mail.vm", "UTF-8", model
        );

        mail.setContent(content);

        if (!mail.send()) {
            log.debug("send new blogs mail fail");
        }

        log.debug("send new blogs mail finish");
    }
}
