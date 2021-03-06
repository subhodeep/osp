package com.flamingos.osp.service;

import javax.servlet.http.HttpServletRequest;

import com.flamingos.osp.bean.OspProfessionalBean;
import com.flamingos.osp.bean.UserBean;
import com.flamingos.osp.dto.OspProfessionalDTO;
import com.flamingos.osp.dto.UserDTO;
import com.flamingos.osp.exception.OSPBusinessException;
import com.flamingos.osp.exception.OspServiceException;

public interface ProfessionalService {
  public UserDTO verifyEmailDataAndUpdateStatus(String username, String UUID, String type)
      throws OSPBusinessException;

  public String verifyAndGenerateNewToken(String username,HttpServletRequest request) throws OSPBusinessException;

  public UserDTO verifyForgotPassword(String username, String UUID, String type)
      throws OSPBusinessException;

  public UserDTO verifyProfessional(String encryptedProfId) throws OSPBusinessException;

  public UserDTO changePassword(UserBean loginBean) throws OSPBusinessException;

  public void saveProfile(OspProfessionalBean professional, HttpServletRequest request)
      throws OspServiceException;

  public void approveProfile(OspProfessionalBean professional, HttpServletRequest request)
      throws OspServiceException;
  
  public OspProfessionalDTO professionalDetailsbyRecordID(long recordId) throws OSPBusinessException;
  
  public OspProfessionalDTO professionalDetailsbyProfId(long profId) throws OSPBusinessException;


}
