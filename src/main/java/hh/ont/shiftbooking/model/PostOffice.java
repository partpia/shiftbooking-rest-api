package hh.ont.shiftbooking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="POST_OFFICE")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class PostOffice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="POST_ID", nullable=false, updatable = false)
    private Long postId;
    
    @Column(name="POSTAL_CODE", length=5, nullable=false)
    @NonNull @NotNull
    private String postalCode;
    
    @Column(name="POST_OFFICE", nullable=false)
    @NonNull @NotNull
    private String city;
    
}
