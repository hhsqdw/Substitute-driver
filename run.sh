#!/bin/bash

# 获取服务名称和额外参数
SERVICE_NAME=$1
shift  # 移除第一个参数（服务名），将剩余参数保存到 $@
EXTRA_ARGS="$@"

# 检查是否输入服务名称
if [ -z "$SERVICE_NAME" ]; then
  echo "Usage: ./run.sh <servicename|all> [additional_maven_args]"
  exit 1
fi

# 定义运行单个服务的函数
run_service() {
  local service=$1
  local args=$2
  echo "Building and running $service with args: $args..."
  mvn clean install -pl $service -am
  if [ $? -ne 0 ]; then
    echo "Failed to build $service. Skipping startup..."
    return 1
  fi
  if [ "$service" == "gateway/" ]; then
    echo "Gateway starting..."
    mvn spring-boot:run -pl $service -Dreactor.netty.http.server.accessLogEnabled=true $args
  else
    mvn spring-boot:run -pl $service $args
  fi
  if [ $? -ne 0 ]; then
    echo "Failed to start $service"
    return 1
  fi
  return 0
}

# 如果输入 "all"，运行所有服务（默认不传参）
if [ "$SERVICE_NAME" == "all" ]; then
  echo "Building and running all services..."
  mvn clean install -pl "!generator"
  if [ $? -ne 0 ]; then
    echo "Failed to build all services. Some modules may be skipped."
  fi
  for module in $(mvn help:evaluate -Dexpression=project.modules -q -DforceStdout | sed -e 's/<[^>]*>//g' -e 's/\s*//g' | tr ',' '\n'); do
    if [ "$module" != "generator" ]; then
      echo "Running $module..."
      run_service $module ""
    fi
  done
else
  # 运行指定的单个服务，并传递额外参数
  run_service $SERVICE_NAME "$EXTRA_ARGS"
fi