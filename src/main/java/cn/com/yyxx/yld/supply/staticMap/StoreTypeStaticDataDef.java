package cn.com.yyxx.yld.supply.staticMap;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum StoreTypeStaticDataDef {
    /**
     *  所有店铺类型静态数据（区分”智慧菜场“、"便利店"）
     */
    OSC_DEPARTMENT("0789ed81fc2e4bd38b60c868aaf11c0d","ac7980e838a84ecaa0312ce3785586ad","OSC_DEPARTMENT","日用百货","014","","",""),
    OSC_FRUIT("0b919e52a4b147a7a273a060145bdd51","ac7980e838a84ecaa0312ce3785586ad","OSC_FRUIT","蔬果类","008","","",""),
    OSC_MEATEGGS("2806da766303417ab91c20f5cb256f43","ac7980e838a84ecaa0312ce3785586ad","OSC_MEATEGGS","鲜肉类","007","","",""),
    OSC_COMMUNITY("2f79c9ba3de14a1e811e22ee3fd3bb27","ac7980e838a84ecaa0312ce3785586ad","OSC_COMMUNITY","社区店","003","","",""),
    OSC_COMMERCIAL("4d62cd50187d417591b183d6e2fe7c42","ac7980e838a84ecaa0312ce3785586ad","OSC_COMMERCIAL","商业店","001","","",""),
    OSC_DElICATESSEN("5a58601d79854977ae58e902feee11de","ac7980e838a84ecaa0312ce3785586ad","OSC_DElICATESSEN","主食熟食","011","","",""),
    OSC_OTHER("5ab7985704b443db8f2ad5ea3c9421f0","ac7980e838a84ecaa0312ce3785586ad","OSC_OTHER","其他店","006","","",""),
    OSC_DRIEDFRUIT("701f153fc8234c79936fcc06c4d286c5","ac7980e838a84ecaa0312ce3785586ad","OSC_DRIEDFRUIT","烟酒零食","013","","",""),
    OSC_DRIED_FRUIT("a455e22f385d4ba5b55afebec1feccbf","ac7980e838a84ecaa0312ce3785586ad","OSC_DRIED_FRUIT","干果特产","012","","",""),
    OSC_GREENOTHER("ab27c2d0e1c745309e13bbb06b0882b1","ac7980e838a84ecaa0312ce3785586ad","OSC_GREENOTHER","其他类","015","","",""),
    OSC_GREASE("bdee48c248a4408aba97e450d5082e30","ac7980e838a84ecaa0312ce3785586ad","OSC_GREASE","粮油调料","009","","",""),
    OSC_STREET("c27a10ea3e1045f39e73f257263ba854","ac7980e838a84ecaa0312ce3785586ad","OSC_STREET","街边店","002","","",""),
    OSC_FISH("ccf002a7ce484d43b3eb7113840b2c12","ac7980e838a84ecaa0312ce3785586ad","OSC_FISH","水产冻品","010","","",""),
    OSC_CONVENIENCE("d89c737cbc6c4228aa013aa8ef4e5f4e","ac7980e838a84ecaa0312ce3785586ad","OSC_CONVENIENCE","便利店","005","","",""),
    OSC_SCHOOL("e70134135fef42ce9959c509d5c89524","ac7980e838a84ecaa0312ce3785586ad","OSC_SCHOOL","校园店","004","","",""),
//    TOP_TYPE_SMARTFOODMARKET("529e549a6cd44e4cbb6b9a7394f1fef8","e9adad61f6cc4c8d832dda6849f81588","TOP_TYPE_SMARTFOODMARKET","菜场店","1","0","2"),
//    TOP_TYPE_CONVENIENCE("ad779ec6d1e24e0eb34a422f21ca57cb","e9adad61f6cc4c8d832dda6849f81588","TOP_TYPE_CONVENIENCE","便利店","0","0","1")
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
    private static final Map<String, StoreTypeStaticDataDef> lookup_sddId = new HashMap();
    private static final Map<String, StoreTypeStaticDataDef> lookup_sddCode = new HashMap();

    static {
        for (StoreTypeStaticDataDef e : EnumSet.allOf(StoreTypeStaticDataDef.class)) {
            lookup_sddId.put(e.sddId, e);
            lookup_sddCode.put(e.sddCode, e);
        }
    };

    public static StoreTypeStaticDataDef findBySddId(String sddId) {
        StoreTypeStaticDataDef value = lookup_sddId.get(sddId);
        if (value == null) {
            return UNKNOW;
        }
        return value;
    };
    public static StoreTypeStaticDataDef findBySddCode(String sddCode){
        StoreTypeStaticDataDef value = lookup_sddCode.get(sddCode);
        if (value == null) {
            return UNKNOW;
        }
        return value;
    };

    StoreTypeStaticDataDef(String sddId, @NotNull String sddDddId, @NotNull String sddCode,
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
