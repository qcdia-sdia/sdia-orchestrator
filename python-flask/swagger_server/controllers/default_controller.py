import connexion
import six

from swagger_server import util


def delete_tosca_template_by_id(id, node_names=None):  # noqa: E501
    """Deletes a tosca topology template

    If the topology is provisoned it will delete the provison (Infrastructure). If it is deployed it will delete the deploymet too (Application) # noqa: E501

    :param id: ID of topology template to return
    :type id: str
    :param node_names: The node(s) to delete
    :type node_names: List[str]

    :rtype: str
    """
    return 'do some magic!'


def deploy_provision_tosca_template_by_id(id):  # noqa: E501
    """deploy the software tosca template

    Returns the deployment ID # noqa: E501

    :param id: ID of topolog template to deploy
    :type id: str

    :rtype: str
    """
    return 'do some magic!'


def get_tosca_template_by_id(id):  # noqa: E501
    """Find topolog template by ID

    Returns a single topolog template # noqa: E501

    :param id: ID of topolog template to return
    :type id: str

    :rtype: str
    """
    return 'do some magic!'


def get_tosca_template_i_ds():  # noqa: E501
    """Get all topolog template IDs

    Returns all IDs # noqa: E501


    :rtype: List[str]
    """
    return 'do some magic!'


def plan_tosca_template_by_id(id):  # noqa: E501
    """plan tosca template

    Returns the ID of the planed topolog template # noqa: E501

    :param id: ID of topolog template to plan
    :type id: str

    :rtype: str
    """
    return 'do some magic!'


def provision_plan_tosca_template_by_id(id):  # noqa: E501
    """provision tosca template

    deletes the provisioned Infrastructure # noqa: E501

    :param id: ID of topolog template to plan
    :type id: str

    :rtype: str
    """
    return 'do some magic!'


def scale_provisioned_tosca_template_by_id(id, node_name, node_num):  # noqa: E501
    """scale tosca template

     # noqa: E501

    :param id: ID of topolog template to scale
    :type id: str
    :param node_name: The node to scale
    :type node_name: str
    :param node_num: The number of nodes to scale
    :type node_num: int

    :rtype: str
    """
    return 'do some magic!'


def update_tosca_template_by_id(id, file=None):  # noqa: E501
    """Updates exisintg topolog template

     # noqa: E501

    :param id: ID of topolog template to return
    :type id: str
    :param file: tosca Template description
    :type file: werkzeug.datastructures.FileStorage

    :rtype: str
    """
    return 'do some magic!'


def upload_tosca_template(file):  # noqa: E501
    """upload a tosca template description file

    uploads and validates TOSCA template file # noqa: E501

    :param file: tosca Template description
    :type file: werkzeug.datastructures.FileStorage

    :rtype: str
    """
    return 'do some magic!'
