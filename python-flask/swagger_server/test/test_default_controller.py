# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.test import BaseTestCase


class TestDefaultController(BaseTestCase):
    """DefaultController integration test stubs"""

    def test_delete_tosca_template_by_id(self):
        """Test case for delete_tosca_template_by_id

        Deletes a tosca topology template
        """
        query_string = [('node_names', 'node_names_example')]
        response = self.client.open(
            '/qcm-api/3.0/tosca_template/{id}'.format(id='id_example'),
            method='DELETE',
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_tosca_template_by_id(self):
        """Test case for get_tosca_template_by_id

        Find topolog template by ID
        """
        response = self.client.open(
            '/qcm-api/3.0/tosca_template/{id}'.format(id='id_example'),
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_tosca_template_i_ds(self):
        """Test case for get_tosca_template_i_ds

        Get all topolog template IDs
        """
        response = self.client.open(
            '/qcm-api/3.0/tosca_template/ids',
            method='GET')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_update_tosca_template_by_id(self):
        """Test case for update_tosca_template_by_id

        Updates exisintg topolog template
        """
        data = dict(file=(BytesIO(b'some file data'), 'file.txt'))
        response = self.client.open(
            '/qcm-api/3.0/tosca_template/{id}'.format(id='id_example'),
            method='PUT',
            data=data,
            content_type='multipart/form-data')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_upload_tosca_template(self):
        """Test case for upload_tosca_template

        upload a tosca template description file
        """
        data = dict(file=(BytesIO(b'some file data'), 'file.txt'))
        response = self.client.open(
            '/qcm-api/3.0/tosca_template',
            method='POST',
            data=data,
            content_type='multipart/form-data')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()