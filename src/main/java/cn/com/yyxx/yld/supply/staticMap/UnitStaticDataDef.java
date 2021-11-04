package cn.com.yyxx.yld.supply.staticMap;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum UnitStaticDataDef {
    /**
     * supply 所有单位的 静态数据
     */
    PCU_CARRY ("096761d93deb464c85458eae05dea753","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_CARRY","提","010","","",""),
    PCU_KG ("1fcb487234d84388a04e4419690670e6","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_KG","KG","019","","",""),
    PCU_CUP ("2659818cf51745ef843e4c67fa7710cc","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_CUP","杯","014","","",""),
    PCU_PIECE ("3bd3e628265941eeb54c8abd46599def","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_PIECE","块","016","","",""),
    PCU_ITEM ("5d18c6c3696445838697c61e60f362df","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_ITEM","件","015","","",""),
    PCU_BAG ("60c73ecc8fa1490b91a9d6c430898ef2","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_BAG","袋","003","","",""),
    PCU_JUP ("6320390b968d4400a3952f328984e8b1","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_JUP","罐","009","","",""),
    PCU_BOX ("6440c3b156754519b44cef3e9d60142b","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_BOX","盒","001","","",""),
    PCU_CASE ("64fecd8b95e4463aba4a95ea4713ada2","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_CASE","套","007","","",""),
    PCU_SLICE ("6b38725ea1bf4844804cc3c2bae2821f","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_SLICE","片","018","","",""),
    PCU_CHEST ("84730dad13024c3b9ea50296e897b790","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_CHEST","箱","011","","",""),
    PCU_ROOT ("8f83b0c73c724733bdbbec4d3ec3b648","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_ROOT","根","017","","",""),
    PCU_BOWL ("9470d5c0a56c495385d2d5fafb41398b","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_BOWL","碗","013","","",""),
    PCU_BARREL ("9d753413e6414df692869a1580e6a2b6","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_BARREL","桶","012","","",""),
    PCU_ONE ("a402fcc7c84c417d862568951c1ebb23","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_ONE","个","021","","",""),
    PCU_SLIP ("b190153adf69401f80131680e11c7cbe","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_SLIP","条","002","","",""),
    PCU_BOTTLE ("c1205c1d34f84cc2ade8131bd623369b","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_BOTTLE","瓶","004","","",""),
    PCU_TIN ("cf2ba8cff0244cef97aeb9e075cdcd7e","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_TIN","听","008","","",""),
    PCU_PACKAGE ("e34cef152cef477eaa6ee2aa8057a875","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_PACKAGE","包","005","","",""),
    PCU_BRANCH ("fac7085b91224e12a67081cf320f5ebd","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_BRANCH","支","006","","",""),
    PCU_TWIN ("fe00beb6ac1f4532a67b784b78f72555","6ef2acc3a82e470e8f3049095e8f3f7b","PCU_TWIN","双","020","","",""),
    //定义未知枚举类型
    UNKNOW(null, null, "UNKNOW", "未知静态变量", null, null, null, null);
    /**
     * 静态数据ID
     */
    private String sddId;
    /**
     * 数据字典ID
     */
    private String sddDddId;
    /**
     * 静态数据编码
     */
    private String sddCode;
    /**
     * 静态数据名称
     */
    private String sddName;
    /**
     * 静态数据值
     */
    private String sddValue;

    /**
     * 数据扩展1
     */
    private String sddExt1;

    /**
     * 数据扩展2
     */
    private String sddExt2;

    /**
     * 数据扩展3
     */
    private String sddExt3;
    /**
     * 这里定义HashMap方便反向查找枚举，若需要其他类型，自行添加
     */
    private static final Map<String, UnitStaticDataDef> lookup_sddId = new HashMap();
    private static final Map<String, UnitStaticDataDef> lookup_sddCode = new HashMap();
    private static final Map<String, UnitStaticDataDef> lookup_sddName = new HashMap();

    static {
        for (UnitStaticDataDef e : EnumSet.allOf(UnitStaticDataDef.class)) {
            lookup_sddId.put(e.sddId, e);
            lookup_sddCode.put(e.sddCode, e);
            lookup_sddName.put(e.sddName, e);
        }
    };

    public static UnitStaticDataDef findBySddId(String sddId) {
        UnitStaticDataDef value = lookup_sddId.get(sddId);
        if (value == null) {
            return UNKNOW;
        }
        return value;
    };
    public static UnitStaticDataDef findBySddName(String sddName) {
        UnitStaticDataDef value = lookup_sddName.get(sddName);
        if (value == null) {
            return UNKNOW;
        }
        return value;
    };
    public static UnitStaticDataDef findBySddCode(String sddCode){
        UnitStaticDataDef value = lookup_sddCode.get(sddCode);
        if (value == null) {
            return UNKNOW;
        }
        return value;
    };

    UnitStaticDataDef(String sddId, @NotNull String sddDddId, @NotNull String sddCode,
                      @NotNull String sddName, @NotNull String sddValue, String sddExt1, String sddExt2,
                      String sddExt3) {
        this.sddId = sddId;
        this.sddDddId = sddDddId;
        this.sddCode = sddCode;
        this.sddName = sddName;
        this.sddValue = sddValue;
        this.sddExt1 = sddExt1;
        this.sddExt2 = sddExt2;
        this.sddExt3 = sddExt3;
    }
}
