package my.e345.ipcounter.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author localEvg
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"PROVIDER_ID", "IP4_ADDR_STR"}))
public class SearchedIpAddr implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "PROVIDER_ID",nullable = false)
    private SearchProvider provider;

    @Column(name = "IP4_ADDR_STR", nullable = false)
    private String ipAddress;

    @Column(nullable = false, length = 1)
    private Character subnet; // Ip4SubnetEnum

    @Column
    private Long count;

    @Column(nullable = false)
    private LocalDateTime queryDateTime;

    public Ip4SubnetEnum getSubnet() {
        if (subnet == null) {
            return null;
        }
        return Ip4SubnetEnum.fromCode(subnet);
    }

    public void setSubnet(Ip4SubnetEnum subnet) {
        if (subnet == null) {
            this.subnet = null;
        } else {
            this.subnet = subnet.code;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SearchProvider getProvider() {
        return provider;
    }

    public void setProvider(SearchProvider provider) {
        this.provider = provider;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        if (ipAddress == null) {
            setSubnet(null);
        } else {
            long ip4addr = Ip4SubnetEnum.parseIp(ipAddress);
            setSubnet(Ip4SubnetEnum.fromIpAddr(ip4addr));
        }
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public LocalDateTime getQueryDateTime() {
        return queryDateTime;
    }

    public void setQueryDateTime(LocalDateTime queryDateTime) {
        this.queryDateTime = queryDateTime;
    }

}
