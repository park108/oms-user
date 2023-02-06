package com.oms.user.entity

import io.swagger.annotations.ApiModelProperty

class Password {

	@ApiModelProperty(notes = "Current password", example = "1q2w3e", required = false)
	var currentPassword: String? = ""

	@ApiModelProperty(notes = "New password", example = "1q2w3e", required = false)
	var newPassword: String? = ""

	@ApiModelProperty(notes = "Initial password", example = "1q2w3e", required = false)
	var initPassword: String? = ""
}