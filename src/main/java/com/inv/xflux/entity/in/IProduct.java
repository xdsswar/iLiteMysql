package com.inv.xflux.entity.in;

import com.inv.xflux.entity.*;

/**
 * @author XDSSWAR
 */
  public interface IProduct {
      long getId();
      String getName();
      String getDescription();
      Category getCategory();
      Provider getProvider();
      Brand getBrand();
      Unit getUnit();
      double getQuantity();
      double getBuyCost();
      double getSellPrice();
}
