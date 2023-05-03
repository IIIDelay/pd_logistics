

package org.iiidev.pinda.authority.entity.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.iiidev.pinda.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@TableName("pd_area")
@ApiModel(
        value = "Area",
        description = "行政区域"
)
public class Area extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("父级区域id")
    @TableField("parent_id")
    private Long parentId;
    @ApiModelProperty("行政区域名称")
    @Length(
            max = 255,
            message = "行政区域名称长度不能超过255"
    )
    @TableField(
            value = "name",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String name;
    @ApiModelProperty("行政区域编码")
    @Length(
            max = 255,
            message = "行政区域编码长度不能超过255"
    )
    @TableField(
            value = "area_code",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String areaCode;
    @ApiModelProperty("城市编码")
    @Length(
            max = 255,
            message = "城市编码长度不能超过255"
    )
    @TableField(
            value = "city_code",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String cityCode;
    @ApiModelProperty("合并名称")
    @Length(
            max = 255,
            message = "合并名称长度不能超过255"
    )
    @TableField(
            value = "merger_name",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String mergerName;
    @ApiModelProperty("简称")
    @Length(
            max = 255,
            message = "简称长度不能超过255"
    )
    @TableField(
            value = "short_name",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String shortName;
    @ApiModelProperty("邮政编码")
    @Length(
            max = 255,
            message = "邮政编码长度不能超过255"
    )
    @TableField(
            value = "zip_code",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String zipCode;
    @ApiModelProperty("行政区域等级（0: 省级 1:市级 2:县级 3:镇级 4:乡村级）")
    @TableField("level")
    private Integer level;
    @ApiModelProperty("经度")
    @Length(
            max = 255,
            message = "经度长度不能超过255"
    )
    @TableField(
            value = "lng",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String lng;
    @ApiModelProperty("纬度")
    @Length(
            max = 255,
            message = "纬度长度不能超过255"
    )
    @TableField(
            value = "lat",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String lat;
    @ApiModelProperty("拼音")
    @Length(
            max = 255,
            message = "拼音长度不能超过255"
    )
    @TableField(
            value = "pinyin",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String pinyin;
    @ApiModelProperty("首字母")
    @Length(
            max = 255,
            message = "首字母长度不能超过255"
    )
    @TableField(
            value = "first",
            condition = "%s LIKE CONCAT('%%',#{%s},'%%')"
    )
    private String first;

    public Area(Long id, Long parentId, String name, String areaCode, String cityCode, String mergerName, String shortName, String zipCode, Integer level, String lng, String lat, String pinyin, String first) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.areaCode = areaCode;
        this.cityCode = cityCode;
        this.mergerName = mergerName;
        this.shortName = shortName;
        this.zipCode = zipCode;
        this.level = level;
        this.lng = lng;
        this.lat = lat;
        this.pinyin = pinyin;
        this.first = first;
    }

    public static AreaBuilder builder() {
        return new AreaBuilder();
    }

    public Long getParentId() {
        return this.parentId;
    }

    public String getName() {
        return this.name;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public String getMergerName() {
        return this.mergerName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public Integer getLevel() {
        return this.level;
    }

    public String getLng() {
        return this.lng;
    }

    public String getLat() {
        return this.lat;
    }

    public String getPinyin() {
        return this.pinyin;
    }

    public String getFirst() {
        return this.first;
    }

    public Area setParentId(final Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Area setName(final String name) {
        this.name = name;
        return this;
    }

    public Area setAreaCode(final String areaCode) {
        this.areaCode = areaCode;
        return this;
    }

    public Area setCityCode(final String cityCode) {
        this.cityCode = cityCode;
        return this;
    }

    public Area setMergerName(final String mergerName) {
        this.mergerName = mergerName;
        return this;
    }

    public Area setShortName(final String shortName) {
        this.shortName = shortName;
        return this;
    }

    public Area setZipCode(final String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public Area setLevel(final Integer level) {
        this.level = level;
        return this;
    }

    public Area setLng(final String lng) {
        this.lng = lng;
        return this;
    }

    public Area setLat(final String lat) {
        this.lat = lat;
        return this;
    }

    public Area setPinyin(final String pinyin) {
        this.pinyin = pinyin;
        return this;
    }

    public Area setFirst(final String first) {
        this.first = first;
        return this;
    }

    public Area() {
    }

    public Area(final Long parentId, final String name, final String areaCode, final String cityCode, final String mergerName, final String shortName, final String zipCode, final Integer level, final String lng, final String lat, final String pinyin, final String first) {
        this.parentId = parentId;
        this.name = name;
        this.areaCode = areaCode;
        this.cityCode = cityCode;
        this.mergerName = mergerName;
        this.shortName = shortName;
        this.zipCode = zipCode;
        this.level = level;
        this.lng = lng;
        this.lat = lat;
        this.pinyin = pinyin;
        this.first = first;
    }

    @Override
    public String toString() {
        return "Area(super=" + super.toString() + ", parentId=" + this.getParentId() + ", name=" + this.getName() + ", areaCode=" + this.getAreaCode() + ", cityCode=" + this.getCityCode() + ", mergerName=" + this.getMergerName() + ", shortName=" + this.getShortName() + ", zipCode=" + this.getZipCode() + ", level=" + this.getLevel() + ", lng=" + this.getLng() + ", lat=" + this.getLat() + ", pinyin=" + this.getPinyin() + ", first=" + this.getFirst() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Area)) {
            return false;
        } else {
            Area other = (Area)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                label157: {
                    Object this$parentId = this.getParentId();
                    Object other$parentId = other.getParentId();
                    if (this$parentId == null) {
                        if (other$parentId == null) {
                            break label157;
                        }
                    } else if (this$parentId.equals(other$parentId)) {
                        break label157;
                    }

                    return false;
                }

                label150: {
                    Object this$name = this.getName();
                    Object other$name = other.getName();
                    if (this$name == null) {
                        if (other$name == null) {
                            break label150;
                        }
                    } else if (this$name.equals(other$name)) {
                        break label150;
                    }

                    return false;
                }

                Object this$areaCode = this.getAreaCode();
                Object other$areaCode = other.getAreaCode();
                if (this$areaCode == null) {
                    if (other$areaCode != null) {
                        return false;
                    }
                } else if (!this$areaCode.equals(other$areaCode)) {
                    return false;
                }

                label136: {
                    Object this$cityCode = this.getCityCode();
                    Object other$cityCode = other.getCityCode();
                    if (this$cityCode == null) {
                        if (other$cityCode == null) {
                            break label136;
                        }
                    } else if (this$cityCode.equals(other$cityCode)) {
                        break label136;
                    }

                    return false;
                }

                Object this$mergerName = this.getMergerName();
                Object other$mergerName = other.getMergerName();
                if (this$mergerName == null) {
                    if (other$mergerName != null) {
                        return false;
                    }
                } else if (!this$mergerName.equals(other$mergerName)) {
                    return false;
                }

                label122: {
                    Object this$shortName = this.getShortName();
                    Object other$shortName = other.getShortName();
                    if (this$shortName == null) {
                        if (other$shortName == null) {
                            break label122;
                        }
                    } else if (this$shortName.equals(other$shortName)) {
                        break label122;
                    }

                    return false;
                }

                Object this$zipCode = this.getZipCode();
                Object other$zipCode = other.getZipCode();
                if (this$zipCode == null) {
                    if (other$zipCode != null) {
                        return false;
                    }
                } else if (!this$zipCode.equals(other$zipCode)) {
                    return false;
                }

                Object this$level = this.getLevel();
                Object other$level = other.getLevel();
                if (this$level == null) {
                    if (other$level != null) {
                        return false;
                    }
                } else if (!this$level.equals(other$level)) {
                    return false;
                }

                Object this$lng = this.getLng();
                Object other$lng = other.getLng();
                if (this$lng == null) {
                    if (other$lng != null) {
                        return false;
                    }
                } else if (!this$lng.equals(other$lng)) {
                    return false;
                }

                label94: {
                    Object this$lat = this.getLat();
                    Object other$lat = other.getLat();
                    if (this$lat == null) {
                        if (other$lat == null) {
                            break label94;
                        }
                    } else if (this$lat.equals(other$lat)) {
                        break label94;
                    }

                    return false;
                }

                label87: {
                    Object this$pinyin = this.getPinyin();
                    Object other$pinyin = other.getPinyin();
                    if (this$pinyin == null) {
                        if (other$pinyin == null) {
                            break label87;
                        }
                    } else if (this$pinyin.equals(other$pinyin)) {
                        break label87;
                    }

                    return false;
                }

                Object this$first = this.getFirst();
                Object other$first = other.getFirst();
                if (this$first == null) {
                    if (other$first != null) {
                        return false;
                    }
                } else if (!this$first.equals(other$first)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Area;
    }

    @Override
    public int hashCode() {
//        int PRIME = true;
        int result = super.hashCode();
        Object $parentId = this.getParentId();
        result = result * 59 + ($parentId == null ? 43 : $parentId.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $areaCode = this.getAreaCode();
        result = result * 59 + ($areaCode == null ? 43 : $areaCode.hashCode());
        Object $cityCode = this.getCityCode();
        result = result * 59 + ($cityCode == null ? 43 : $cityCode.hashCode());
        Object $mergerName = this.getMergerName();
        result = result * 59 + ($mergerName == null ? 43 : $mergerName.hashCode());
        Object $shortName = this.getShortName();
        result = result * 59 + ($shortName == null ? 43 : $shortName.hashCode());
        Object $zipCode = this.getZipCode();
        result = result * 59 + ($zipCode == null ? 43 : $zipCode.hashCode());
        Object $level = this.getLevel();
        result = result * 59 + ($level == null ? 43 : $level.hashCode());
        Object $lng = this.getLng();
        result = result * 59 + ($lng == null ? 43 : $lng.hashCode());
        Object $lat = this.getLat();
        result = result * 59 + ($lat == null ? 43 : $lat.hashCode());
        Object $pinyin = this.getPinyin();
        result = result * 59 + ($pinyin == null ? 43 : $pinyin.hashCode());
        Object $first = this.getFirst();
        result = result * 59 + ($first == null ? 43 : $first.hashCode());
        return result;
    }

    public static class AreaBuilder {
        private Long id;
        private Long parentId;
        private String name;
        private String areaCode;
        private String cityCode;
        private String mergerName;
        private String shortName;
        private String zipCode;
        private Integer level;
        private String lng;
        private String lat;
        private String pinyin;
        private String first;

        AreaBuilder() {
        }

        public AreaBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        public AreaBuilder parentId(final Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public AreaBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public AreaBuilder areaCode(final String areaCode) {
            this.areaCode = areaCode;
            return this;
        }

        public AreaBuilder cityCode(final String cityCode) {
            this.cityCode = cityCode;
            return this;
        }

        public AreaBuilder mergerName(final String mergerName) {
            this.mergerName = mergerName;
            return this;
        }

        public AreaBuilder shortName(final String shortName) {
            this.shortName = shortName;
            return this;
        }

        public AreaBuilder zipCode(final String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public AreaBuilder level(final Integer level) {
            this.level = level;
            return this;
        }

        public AreaBuilder lng(final String lng) {
            this.lng = lng;
            return this;
        }

        public AreaBuilder lat(final String lat) {
            this.lat = lat;
            return this;
        }

        public AreaBuilder pinyin(final String pinyin) {
            this.pinyin = pinyin;
            return this;
        }

        public AreaBuilder first(final String first) {
            this.first = first;
            return this;
        }

        public Area build() {
            return new Area(this.id, this.parentId, this.name, this.areaCode, this.cityCode, this.mergerName, this.shortName, this.zipCode, this.level, this.lng, this.lat, this.pinyin, this.first);
        }

        @Override
        public String toString() {
            return "Area.AreaBuilder(id=" + this.id + ", parentId=" + this.parentId + ", name=" + this.name + ", areaCode=" + this.areaCode + ", cityCode=" + this.cityCode + ", mergerName=" + this.mergerName + ", shortName=" + this.shortName + ", zipCode=" + this.zipCode + ", level=" + this.level + ", lng=" + this.lng + ", lat=" + this.lat + ", pinyin=" + this.pinyin + ", first=" + this.first + ")";
        }
    }
}
