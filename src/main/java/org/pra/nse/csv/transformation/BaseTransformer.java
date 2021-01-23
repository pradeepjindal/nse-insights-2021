package org.pra.nse.csv.transformation;

import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;


public class BaseTransformer {

    protected final TransformationHelper transformationHelper;
    protected final NseFileUtils nseFileUtils;
    protected final PraFileUtils praFileUtils;

    public BaseTransformer(TransformationHelper transformationHelper,
                           NseFileUtils nseFileUtils,
                           PraFileUtils praFileUtils) {
        this.transformationHelper = transformationHelper;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
    }

}
