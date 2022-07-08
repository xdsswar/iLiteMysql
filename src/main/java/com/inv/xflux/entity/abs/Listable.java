package com.inv.xflux.entity.abs;

import com.inv.xflux.entity.Brand;
import com.inv.xflux.entity.Category;
import com.inv.xflux.entity.Employee;
import com.inv.xflux.entity.Provider;

/**
 * @author  XDSSWAR
 */
public abstract class Listable {

    public String getName(){
        return null;
    }
    public String getDescription(){
        return null;
    }
    public String getSymbol(){
        return null;
    }
    public String getPhone(){
        return null;
    }
    public double getTaxVal(){
        return 0;
    }
    public String getEmail(){return  null;}
    public double getQuantity(){return 0;}
    //For Products
    public Category getCategory(){
        return null;
    }
    public Brand getBrand(){
        return null;
    }
    public Provider getProvider(){
        return null;
    }
    public double getSellPrice(){
        return 0;
    }
}

