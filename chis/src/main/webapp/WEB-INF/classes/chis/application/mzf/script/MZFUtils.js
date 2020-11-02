$package("chis.application.hy.script");

chis.application.hy.script.HypertensionUtils = {
    isBPNormal : function(constriction, diastolic) {
        if (constriction >= 180 || diastolic >= 110) {
            return false; // @@ 3级（重度）
        }
        if ((constriction >= 160 && constriction <= 179)
            || (diastolic >= 100 && diastolic <= 109)) {
            return false; // @@ 2级（中度）
        }
        if ((constriction >= 140 && constriction <= 159)
            || (diastolic >= 90 && diastolic <= 99)) {
                return false; // @@ 1级（轻度）
        }
        if (constriction < 120 && diastolic < 80) {
            return true; // @@ 理想血压
        }
        if (constriction < 130 && diastolic < 85) {
            return true; // @@ 正常血压
        }
        if ((constriction >= 130 && constriction <= 139 && diastolic < 90)
            || (diastolic >= 85 && diastolic <= 89 && constriction < 140)) {
            return true; // @@ 正常高值
        }
        if (constriction >= 140 && diastolic < 90) {
            return false; // @@ 单纯收缩性高血压
        }
        return true;
    }
};