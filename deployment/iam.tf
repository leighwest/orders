resource "aws_iam_user" "smtp_user" {
  name = "ses-smtp-user"
}

resource "aws_iam_user_policy_attachment" "ses_send_policy" {
  user       = aws_iam_user.smtp_user.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSESFullAccess"
}

resource "aws_iam_access_key" "smtp_credentials" {
  user = aws_iam_user.smtp_user.name
}

resource "aws_ssm_parameter" "smtp_username" {
  name  = "orders_iam_access_key_id"
  type  = "String"
  value = aws_iam_access_key.smtp_credentials.id
}

resource "aws_ssm_parameter" "smtp_password" {
  name  = "orders_iam_secret_access_key"
  type  = "SecureString"
  value = aws_iam_access_key.smtp_credentials.secret
}
