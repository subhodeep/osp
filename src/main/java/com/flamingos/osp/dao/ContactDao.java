package com.flamingos.osp.dao;

import com.flamingos.osp.bean.OspProfessionalBean;
import com.flamingos.osp.exception.OspDaoException;

public interface ContactDao {

  public void addContact(OspProfessionalBean professionalBean) throws OspDaoException;

}
