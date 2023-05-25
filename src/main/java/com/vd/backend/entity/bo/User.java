package com.vd.backend.entity.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

    /**
     *
     * @TableName user
     */
    @TableName(value ="user")
    public class User implements Serializable {
        /**
         *
         */
        @TableId
        private String id;

        /**
         *
         */
        private String token;

        /**
         *
         */
        private Date expiredTime;

        /**
         *
         */
        private String fhirId;

        /**
         *
         */
        private Integer role;

        @TableField(exist = false)
        private static final long serialVersionUID = 1L;

        /**
         *
         */
        public String getId() {
            return id;
        }

        /**
         *
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         */
        public String getToken() {
            return token;
        }

        /**
         *
         */
        public void setToken(String token) {
            this.token = token;
        }

        /**
         *
         */
        public Date getExpiredTime() {
            return expiredTime;
        }

        /**
         *
         */
        public void setExpiredTime(Date expiredTime) {
            this.expiredTime = expiredTime;
        }

        /**
         *
         */
        public String getFhirId() {
            return fhirId;
        }

        /**
         *
         */
        public void setFhirId(String fhirId) {
            this.fhirId = fhirId;
        }

        /**
         *
         */
        public Integer getRole() {
            return role;
        }

        /**
         *
         */
        public void setRole(Integer role) {
            this.role = role;
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null) {
                return false;
            }
            if (getClass() != that.getClass()) {
                return false;
            }
            User other = (User) that;
            return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getToken() == null ? other.getToken() == null : this.getToken().equals(other.getToken()))
                && (this.getExpiredTime() == null ? other.getExpiredTime() == null : this.getExpiredTime().equals(other.getExpiredTime()))
                && (this.getFhirId() == null ? other.getFhirId() == null : this.getFhirId().equals(other.getFhirId()))
                && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
            result = prime * result + ((getToken() == null) ? 0 : getToken().hashCode());
            result = prime * result + ((getExpiredTime() == null) ? 0 : getExpiredTime().hashCode());
            result = prime * result + ((getFhirId() == null) ? 0 : getFhirId().hashCode());
            result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName());
            sb.append(" [");
            sb.append("Hash = ").append(hashCode());
            sb.append(", id=").append(id);
            sb.append(", token=").append(token);
            sb.append(", expiredTime=").append(expiredTime);
            sb.append(", fhirId=").append(fhirId);
            sb.append(", role=").append(role);
            sb.append(", serialVersionUID=").append(serialVersionUID);
            sb.append("]");
            return sb.toString();
        }
}
