/**
 * 
 */
package com.p3.archon;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.p3.archon.common.constants.APIResponseMessages;

/**
 * @author saideepak
 *
 */
@RestController
public class BaseController {

	@GetMapping("/")
	public String index() {
		return APIResponseMessages.APPLICATION_RESPONSE;
	}
}
