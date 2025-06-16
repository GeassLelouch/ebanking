package ebanking.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ebanking.dto.PagedResponse;
import ebanking.dto.TransactionDTO;
import ebanking.security.CustomUserDetails;
import ebanking.security.JwtUtil;
import ebanking.service.TransactionService;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private TransactionService txService;
  @MockBean
  private JwtUtil jwtUtil;
  @MockBean
  private AuthenticationManager authManager;
  
  @BeforeEach
  void setupAuthentication() {
      // 1. 準備一組授權角色
      List<GrantedAuthority> authorities = List.of(
          new SimpleGrantedAuthority("ROLE_USER")
      );

      // 2. 建立一個 CustomUserDetails 實例
      CustomUserDetails me = new CustomUserDetails(
          "alice@example.com",      // username
          "dummy-password",         // password
          "CUST-000123",            // customerId
          authorities               // 角色清單
      );

      // 3. 建立 Authentication 並放進 SecurityContext
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(me, null, me.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  @WithMockUser(username = "user1", roles = {"USER"})
  void testGetTransactionsByMonth() throws Exception {
	  UUID id = UUID.randomUUID();
	  String iban = "CH93000000000000000000";
	  String customerId = "cust-123";
	  
	  
	  TransactionDTO dto = new TransactionDTO(
		    id.toString(),       // 把 UUID 轉成 String
		    iban,                // accountIban
		    customerId,          // customerId
		    BigDecimal.valueOf(120),  // amount
		    "EUR",               // currency
		    LocalDate.of(2022, 10, 20), // valueDate
		    "IntegrationTest"    // description
		);
	  
	  BigDecimal sum = dto.getAmount();
    
	  PagedResponse<TransactionDTO> page = new PagedResponse<TransactionDTO>(
			    List.of(dto),    // content
			    0,               // pageNumber
			    10,              // pageSize
			    1L,              // totalElements
			    1,               // totalPages
			    true,            // last page?
			    sum              // sumAmountInBaseCurrency
			);
    when(txService.getTransactionsByMonth(
         /* iban */ "CH93000000000000000000",
         /* year */ 2022,
         /* month */ 10,
         /* page */ 0,
         /* size */ 10,null
    )).thenReturn(page);

    // 3) Mock JWT 驗證：假 token 不做真檢查
    when(jwtUtil.validateToken("fake-token",null)).thenReturn(true);
    when(jwtUtil.extractUsername("fake-token"))
      .thenReturn("CH93000000000000000000");

    // 4) 執行 GET /api/transactions
    mvc.perform(get("/api/transactions")
        .param("iban", "CH93000000000000000000")
        .param("year", "2022")
        .param("month", "10")
        .param("page", "0")
        .param("size", "10")
        .header("Authorization", "Bearer fake-token")
        .accept(MediaType.APPLICATION_JSON)
    )
    .andExpect(status().isOk())
    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }
}
