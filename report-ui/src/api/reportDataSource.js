import request from '@/utils/request'

export function reportDataSourceList(params) {
  return request({
    url: 'dataSource/pageList',
    method: 'GET',
    params,
  })
}

export function reportDataSourceAdd(data) {
  return request({
    url: 'dataSource',
    method: 'post',
    data
  })
}

export function reportDataSourceDeleteBatch(data) {
  return request({
    url: 'dataSource/delete/batch',
    method: 'post',
    data
  })
}

export function reportDataSourceUpdate(data) {
  return request({
    url: 'dataSource',
    method: 'put',
    data
  })
}

export function reportDataSourceDetail(data) {
  return request({
    url: 'dataSource/' + data.id,
    method: 'get',
    params: { accessKey: data.accessKey }
  })
}

export function testConnection (data) {
  return request({
    url: '/dataSource/testConnection',
    method: 'post',
    data,
  })
}

export function getSQLiteTables (data) {
  return request({
    url: '/dataSource/getSQLiteTables',
    method: 'post',
    data,
  })
}

export function getSQLiteTableColumns (data, tableName) {
  return request({
    url: '/dataSource/getSQLiteTableColumns',
    method: 'post',
    data,
    params: { tableName }
  })
}

export function uploadSQLiteFile (file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/dataSource/uploadSQLiteFile',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export default { 
  reportDataSourceList, 
  reportDataSourceAdd, 
  reportDataSourceDeleteBatch, 
  reportDataSourceUpdate, 
  reportDataSourceDetail, 
  testConnection, 
  getSQLiteTables, 
  getSQLiteTableColumns, 
  uploadSQLiteFile 
}
