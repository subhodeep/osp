package com.flamingos.osp.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.flamingos.osp.bean.ConfigParamBean;
import com.flamingos.osp.bean.OspProfessionalBean;
import com.flamingos.osp.dao.ProfessionalDao;
import com.flamingos.osp.dto.ConfigParamDto;
import com.flamingos.osp.exception.OSPBusinessException;
import com.flamingos.osp.service.AdminService;
import com.flamingos.osp.util.AppConstants;

@Transactional(propagation = Propagation.REQUIRED)
@Service
public class AdminServiceImpl implements AdminService {


  @Autowired
  private ConfigParamBean configParamBean;

  @Autowired
  ProfessionalDao profDao;

  ConfigParamDto userStatusBean = null;


  @Override
  public String approveProfile(OspProfessionalBean professional, HttpServletRequest request)
      throws OSPBusinessException {
    userStatusBean =
        configParamBean.getParameterByCodeName(AppConstants.PARAM_CODE_USER_STATUS,
            AppConstants.PARAM_NAME_INITIAL);
    try {
      profDao.approveProfile(professional, 1);
    } catch (Exception ex) {
      throw new OSPBusinessException(AppConstants.PROFESSIONAL_ADD_PROFILE_MODULE,
          AppConstants.PROFESSIONAL_ADD_PROFILE_EXCEPTION_ERRCODE,
          AppConstants.PROFESSIONAL_ADD_PROFILE_EXCEPTION_ERRDESC, ex);

    }

    return null;

  }

}
