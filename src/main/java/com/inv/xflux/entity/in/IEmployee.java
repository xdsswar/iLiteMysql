package com.inv.xflux.entity.in;

import com.inv.data.access.emums.Role;
import com.inv.data.access.emums.EmployeeStatus;
import javafx.scene.image.Image;

/**
 * @author XDSSWAR
 */
  public interface IEmployee {
      long getId();
      String getName();
      String getLastName();
      String getUser();
      String getPhone();
      String getEmail();
      String getAddress();
      String getBirthDate();
      EmployeeStatus getStatus();
      Role getRole();
      Image getPicture();
}
