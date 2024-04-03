resource "aws_instance" "orders" {
  ami           = "ami-0e29b5d20780849a6"
  instance_type = "t3.small"
  tags = {
    Name = "orders-server"
  }

  vpc_security_group_ids = [aws_security_group.default.id]

  user_data = data.cloudinit_config.instance-bootstrap.rendered

  key_name = aws_key_pair.mykeypair.key_name
}

resource "aws_eip" "orders-eip" {
  instance = aws_instance.orders.id
}
