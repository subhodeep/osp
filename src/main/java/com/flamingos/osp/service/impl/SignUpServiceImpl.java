package com.flamingos.osp.service.impl;

import java.util.UUID;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import com.flamingos.osp.bean.UserBean;
import com.flamingos.osp.dao.SignUpDao;
import com.flamingos.osp.dto.UserDTO;
import com.flamingos.osp.exception.OspDaoException;
import com.flamingos.osp.exception.OspServiceException;
import com.flamingos.osp.service.EmailService;
import com.flamingos.osp.service.SignUpService;
import com.flamingos.osp.service.SmsService;
import com.flamingos.osp.util.EncoderDecoderUtil;

@Service
@Configuration
@PropertySource("classpath:osp.properties")
public class SignUpServiceImpl implements SignUpService {

  @Value("${email.expire.time}")
  private String emailExpireTime;

  @Value("${sms.expire.time}")
  private String smsExpireTime;

  @Autowired
  SignUpDao signUpDao;

  @Autowired
  EncoderDecoderUtil encDecUtil;

  @Autowired
  EmailService emailService;

  @Autowired
  SmsService smsService;

  private static final Logger logger = Logger.getLogger(SignUpServiceImpl.class);

  @Override
  public UserDTO createUser(UserBean userBean, HttpServletRequest request)
      throws OspServiceException {
    try {
      UserDTO userDto = new UserDTO();
      logger.debug("create User");
      checkUniqueness(userBean);
      String returnMessage =
          createNewUser(userBean, request, Integer.parseInt(emailExpireTime),
              Integer.parseInt(smsExpireTime));
      if (returnMessage.equals("success")) {
        userDto = signUpDao.findByUserName(userBean.getUserName());
        if (userBean.getProf_id() != null) {
          UserDTO prof = signUpDao.checkForProfessional(userBean);
          signUpDao.mapUserAndProfessional(userDto.getUserId(), prof.getUserId());
        }
        userBean.setEmail(userDto.getEmail());
        userBean.setContactNumber(Long.parseLong(userDto.getUserContact()));
        String userMessageForEmail = sendVerificationLinkinEmail(userBean, request);
        String userMessageForSMS = sendVerificationLinkinSms(userBean, request);
        logger.info("verfication email  link send" + userMessageForEmail);
        logger.info("verfication sms link send" + userMessageForSMS);
        userDto.setReturnStatus("success");
        userDto.setReturnMessage("user created succcessfully");
        return userDto;
      } else {

        userDto.setReturnStatus("fail");
        userDto.setReturnMessage("fail to create user");
        return userDto;
      }
    } catch (OspDaoException e) {
      throw new OspServiceException(e);
    }
  }

  @Override
  public void checkUniqueness(UserBean loginBean) throws OspServiceException {

    try {
      UserDTO userDTOForUserName = signUpDao.findByUserName(loginBean.getUserName());
      if (userDTOForUserName != null) {
        throw new OspDaoException("UserName already exists. Please use different one.");

      }
      UserDTO userDTOForContact = signUpDao.findByContact(loginBean.getContactNumber());
      if (userDTOForContact != null) {
        throw new OspDaoException(
            "Contact Number already exists. Please use forgot username/pass to retrieve account details if not able to login.");
      }
      UserDTO userDTOForEmail = signUpDao.findByEmailAddress(loginBean.getEmail());
      if (userDTOForEmail != null) {
        throw new OspDaoException(
            "Email id already exists. Please use forgot username/pass to retrieve account details if not able to login.");
      }

      // logger.debug("Creating user......");

    } catch (OspDaoException exp) {
      throw new OspServiceException(exp);
    }

  }

  private String createNewUser(UserBean userBean, HttpServletRequest request, int emailExpireTime,
      int smsExpireTime) throws OspServiceException {
    try {
      userBean.setEmailUUID(String.valueOf(UUID.randomUUID()));
      userBean.setSmsUUID(String.valueOf(UUID.randomUUID()));
      String encryptedPassword = encDecUtil.getEncodedValue(userBean.getPassword());
      userBean.setUserName(userBean.getUserName());
      userBean.setPassword(encryptedPassword);
      userBean.setActiveStatus(0);
      userBean.setEmailVerified(0);
      userBean.setSmsVerfied(0);
      userBean.setUserTypeCD(1); // TODO .. What is this variable.
      userBean.setFirstName(userBean.getFirstName());
      userBean.setMiddleName(userBean.getMiddleName());
      userBean.setLastName(userBean.getLastName());
      userBean.setRole_id(userBean.getRole_id());
      signUpDao.createNewUser(userBean, emailExpireTime, smsExpireTime);
      return "success";
    } catch (Exception e) {
      throw new OspServiceException(e);
    }

  }

  @Override
  public String sendVerificationLinkinEmail(UserBean userBean, HttpServletRequest request)
      throws OspServiceException {
    logger.debug("sending mail... ");
    String encryptedUserName = encDecUtil.getEncodedValue(userBean.getUserName());
    String Uuid = userBean.getEmailUUID();
    String linkTobeSend =
        request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
            + request.getContextPath() + "/verifyEmail?username=" + encryptedUserName + "&UUID="
            + Uuid;
    // emailService.sendMail("REGISTRATION_MAIL", userBean.getEmail(),
    // linkTobeSend,"Registration Email");
    logger.debug("Email sent... ");
    return linkTobeSend;

  }

  @Override
  public String sendVerificationLinkinSms(UserBean userBean, HttpServletRequest request)
      throws OspServiceException {
    logger.debug("sending SMS... ");
    String encryptedUserName = encDecUtil.getEncodedValue(userBean.getUserName());
    String Uuid = userBean.getSmsUUID();
    String linkTobeSend =
        request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
            + request.getContextPath() + "/verifySms?username=" + encryptedUserName + "&UUID="
            + Uuid;
    // smsService.sendSms(String.valueOf(userBean.getContactNumber()), linkTobeSend);
    logger.debug("SMS SENT  ... ");
    return linkTobeSend;

  }

  @Override
  public void deleteUser(UserBean ub) {
    // TODO Auto-generated method stub

  }

  @Override
  public String checkUserName(UserBean userBean) throws OspServiceException {
    try {
      UserDTO userDTOForUserName = signUpDao.findByUserName(userBean.getUserName());
      if (userDTOForUserName != null) {
        throw new OspServiceException("UserName already exists. Please use different one.");
      }
      return "success";
    } catch (OspDaoException exp) {
      logger.error(exp);
      throw new OspServiceException(exp);
    }

  }
}
