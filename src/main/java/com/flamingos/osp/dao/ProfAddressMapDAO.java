package com.flamingos.osp.dao;

import com.flamingos.osp.bean.OspProfessionalBean;
import com.flamingos.osp.exception.OspDaoException;

public interface ProfAddressMapDAO {

  public void saveAddressMap(OspProfessionalBean professionalBean) throws OspDaoException;

}
