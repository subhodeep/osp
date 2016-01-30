package com.flamingos.osp.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flamingos.osp.dto.ConfigParamDTO;
import com.flamingos.osp.exception.OSPBusinessException;
import com.flamingos.osp.exception.OSPErrorHandler;
import com.flamingos.osp.service.ConfigParamLoaderService;
import com.flamingos.osp.util.AppConstants;
import com.flamingos.osp.util.AppUtil;

@Component
public class ConfigParamBean {

  @Autowired
  private ConfigParamLoaderService configParamLoaderService;
  @Autowired
  private OSPErrorHandler ospErrorHandler;

  /* OSP Parameters */
  private List<ConfigParamDTO> listParam = new ArrayList<ConfigParamDTO>();
  private Map<String, ConfigParamDTO> mapByParamId = new HashMap<String, ConfigParamDTO>();
  private Map<String, ConfigParamDTO> mapByParamCodeAndName = new HashMap<String, ConfigParamDTO>();
  private Map<String, List<ConfigParamDTO>> mapByParamCode =
      new HashMap<String, List<ConfigParamDTO>>();

  /* Communication Templates */
  private List<TemplateBean> templateBeanList = new ArrayList<TemplateBean>();
  private Map<String, TemplateBean> templateMapByName = new HashMap<String, TemplateBean>();
  private Map<String, TemplateBean> templateMapById = new HashMap<String, TemplateBean>();

  /* OSP Role */
  private List<RoleBean> lstRoles = null;
  private Map<String, RoleBean> roleMapByName = new HashMap<String, RoleBean>();
  private Map<Integer, RoleBean> roleMapById = new HashMap<Integer, RoleBean>();


  public void loadConfigParam() {
    Map<String, String> logMap = new HashMap<String, String>();
    logMap.put("Message", "Config Param Loading Started");
    AppUtil.writeToLog(AppConstants.CONFIG_LOADING_MODULE, AppConstants.LOG_TYPE_INFO, logMap);
    try {
      listParam = configParamLoaderService.loadConfigParam();
      for (ConfigParamDTO param : listParam) {
        mapByParamId.put(Integer.toString(param.getParameterid()), param);
        mapByParamCodeAndName.put(param.getCode() + "__" + param.getName(), param);
        if (mapByParamCode.containsKey(param.getCode())) {
          List<ConfigParamDTO> existlistparamDto = mapByParamCode.get(param.getCode());
          existlistparamDto.add(param);
        } else {
          List<ConfigParamDTO> newlistparamDto = new ArrayList<ConfigParamDTO>();
          newlistparamDto.add(param);
          mapByParamCode.put(param.getCode(), newlistparamDto);
        }
      }
      templateBeanList = configParamLoaderService.getAllTemplate();
      for (TemplateBean tb : templateBeanList) {
        templateMapByName.put(tb.getTempName(), tb);
        templateMapById.put(Integer.toString(tb.getTemplateId()), tb);
      }


      lstRoles = configParamLoaderService.getAllRoles();
      if (null != lstRoles) {
        for (RoleBean oRoleBean : lstRoles) {
          roleMapById.put(oRoleBean.getRoleId(), oRoleBean);
          roleMapByName.put(oRoleBean.getRoleName(), oRoleBean);
        }
      }
    } catch (OSPBusinessException ospEx) {
      if ("".equalsIgnoreCase(ospEx.getModuleName())) {
        ospEx.setModuleName(AppConstants.CONFIG_LOADING_MODULE);
      }
      ospErrorHandler.handleOspBusinessException(ospEx);

    } catch (Exception gne) {
      ospErrorHandler.handleGenericException(AppConstants.CONFIG_LOADING_MODULE, gne);
    }

  }

  public TemplateBean getTemplateByName(String name) {
    return templateMapByName.get(name);
  }

  public TemplateBean getTemplateById(String id) {
    return templateMapByName.get(id);
  }

  public ConfigParamDTO getParameterByCodeName(String paramCode, String paramName) {
    String key = paramCode + "__" + paramName;
    return getMapByParamCodeAndName().get(key);
  }

  public Map<String, ConfigParamDTO> getMapByParamCodeAndName() {
    return mapByParamCodeAndName;
  }

  public List<RoleBean> getAllRoles() {
    return lstRoles;
  }

  public RoleBean getRoleByName(String roleName) {
    return roleMapByName.get(roleName);
  }

  public RoleBean getRoleById(int roleId) {
    return roleMapById.get(roleId);
  }
}
