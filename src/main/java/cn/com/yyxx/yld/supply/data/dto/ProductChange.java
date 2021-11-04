package cn.com.yyxx.yld.supply.data.dto;


import lombok.Data;

import java.util.List;

@Data
public class ProductChange {

    /**
     * 门店ID
     */
    String store_id;
    /**
     * 设备编号
     */
    String serial_num;

    /**
     * 收银员姓名
     */
    private String cashierName;
    /**
     *收银员编号
     */
    String user_no;
    /**
     * 收银员工号
     */
    String job_no;
    /**
     * AddProduct,新增商品
     * AddStock, 库存修改,主要用于主动修改库存
     * Change, 修改商品, 只能修改进价,售价
     * ChangeStock,
     * SaleProduct, 售卖
     * DelProduct, 删除
     * Refund      退款
     */
    String typed;

    /**
     * cmd协议
     */
    String cmd;

    /**
     * 返回值状态,成功返回true;失败返回false
     */
    boolean status;
    //////////////////////////////////////////////////////////////////////////////////////
    /**
     * 商品实体
     */
    List<ProductItem> productItems;
}
